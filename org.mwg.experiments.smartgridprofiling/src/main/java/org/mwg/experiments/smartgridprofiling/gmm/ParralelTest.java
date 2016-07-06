package org.mwg.experiments.smartgridprofiling.gmm;

import org.mwg.*;
import org.mwg.core.scheduler.ExecutorScheduler;
import org.mwg.importer.ImporterPlugin;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.algorithm.profiling.GaussianTreeNode;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionConditional;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import static org.mwg.importer.ImporterActions.readFiles;
import static org.mwg.task.Actions.action;
import static org.mwg.task.Actions.split;
import static org.mwg.task.Actions.then;

/**
 * Created by assaad on 06/07/16.
 */
public class ParralelTest {
    public static void main(String[] arg){
        final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
        final double[] err = new double[]{0.5 * 0.5, 0.25 * 0.25, 0.9 * 0.9, 10 * 10};

        final int[] counter = new int[1];
        final long[] starttime=new long[1];
        counter[0]=0;

        final Graph g = new org.mwg.GraphBuilder()
                .withPlugin(new ImporterPlugin())
                .withMemorySize(3000000)
                .saveEvery(20000)
                // .withOffHeapMemory()
                .withStorage(new LevelDBStorage(csvdir + "leveldb/").useNative(false))
                .withPlugin(new MLPlugin())

                .withScheduler(new ExecutorScheduler())
                .build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                starttime[0]=System.nanoTime();
                final Task t = readFiles(csvdir+"NDsim/allusers/")
                        .foreachPar(
                                then(new Action() {
                                    @Override
                                    public void eval(TaskContext context) {
                                        //create node and set as var

                                        GaussianTreeNode profiler = (GaussianTreeNode) context.graph().newTypedNode(0,0,GaussianTreeNode.NAME);
                                        profiler.set(GaussianMixtureNode.PRECISION, err); //Minimum covariance in both axis
                                        context.setVariable("profiler", profiler);
                                        profiler.free();
                                        context.setUnsafeResult(context.result());

                                    }
                                }).ifThen(new TaskFunctionConditional() {
                                    @Override
                                    public boolean eval(TaskContext context) {
                                        return context.resultAsString().contains("csv");
                                    }
                                }, action("readLines", "{{result}}")
                                        .foreach(
                                                split(",").then(new Action() {
                                                    @Override
                                                    public void eval(TaskContext context) {
                                                        String[] values = context.resultAsStringArray();
                                                        double[] vector=new double[4];
                                                        double[] features=new double[3];


                                                        vector[0]=Double.parseDouble(values[1]);
                                                        vector[1]=Double.parseDouble(values[2]);
                                                        vector[2]=Double.parseDouble(values[3]);
                                                        vector[3]=Double.parseDouble(values[4]);

                                                        System.arraycopy(vector, 0, features, 0, vector.length - 1);


                                                        GaussianTreeNode profiler = (GaussianTreeNode) context.variable("profiler");
                                                       profiler.internalLearn(vector, features, new Callback<Boolean>() {
                                                            @Override
                                                            public void on(Boolean result) {
                                                                counter[0]++;
                                                                if(counter[0]%10000==0){
                                                                    long end=System.nanoTime();
                                                                    double time=end-starttime[0];
                                                                    time=time/1000000000;
                                                                    time=counter[0]/time;
                                                                    double d= counter[0];
                                                                    d=d/1000000;
                                                                    System.out.println(d+" m "+time+" values/sec");
                                                                }
                                                            }
                                                        });



                                                        //TODO lookup and train

                                                        //end
                                                        context.setUnsafeResult(null);


                                                    }
                                                })
                                        )
                        ));
                t.executeWith(g, null, null, false, new Callback<Object>() {
                    @Override
                    public void on(Object result) {
                        System.out.println("end!");
                    }
                });
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
