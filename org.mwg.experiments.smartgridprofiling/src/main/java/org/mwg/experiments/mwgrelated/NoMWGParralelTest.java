package org.mwg.experiments.mwgrelated;

import org.mwg.*;
import org.mwg.experiments.kdtree.GaussianTreeNode;
import org.mwg.importer.ImporterPlugin;
import org.mwg.task.*;

import java.util.concurrent.atomic.AtomicLong;

import static org.mwg.core.task.Actions.action;
import static org.mwg.core.task.Actions.ifThen;
import static org.mwg.core.task.Actions.newTask;
import static org.mwg.importer.ImporterActions.readFiles;
import static org.mwg.importer.ImporterActions.split;

/**
 * Created by assaad on 06/07/16.
 */
public class NoMWGParralelTest {
    public static void main(String[] arg) {
        final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
        final double[] err = new double[]{0.5 * 0.5, 0.25 * 0.25, 0.9 * 0.9, 10 * 10};

        final AtomicLong counter = new AtomicLong(0);
        final AtomicLong starttime = new AtomicLong(0);


        final Graph g = new GraphBuilder()
                .withPlugin(new ImporterPlugin())
                .withMemorySize(3000000)
                .withStorage(new LevelDBStorage(csvdir + "leveldb/").useNative(false))
                //.withScheduler(new ExecutorScheduler().workers(3))
                .build();

        DeferCounterSync waiter = g.newSyncCounter(1);

        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                starttime.set(System.nanoTime());
                final Task t = newTask()
                        .then(readFiles(csvdir + "NDsim/allusers/"))
                        .forEachPar(
                                ifThen(new ConditionalFunction() {
                                           @Override
                                           public boolean eval(TaskContext context) {
                                               return context.result().get(0).toString().contains("csv");
                                           }
                                       },
                                        newTask()
                                                .thenDo(new ActionFunction() {
                                                    @Override
                                                    public void eval(TaskContext context) {
                                                        //create node and set as var

                                                        GaussianTreeNode profiler = new GaussianTreeNode();
                                                        profiler.setPrecisions(err);
                                                        context.setGlobalVariable("profiler", context.wrap(profiler));
                                                        context.continueTask();

                                                    }
                                                })
                                                .then(action("readLines", "{{result}}"))
                                                .forEach(
                                                        newTask()
                                                                .then(split(","))
                                                                .thenDo(new ActionFunction() {
                                                                    @Override
                                                                    public void eval(TaskContext context) {
                                                                        TaskResult<String> values = context.result();
                                                                        double[] vector = new double[4];
                                                                        double[] features = new double[3];


                                                                        vector[0] = Double.parseDouble(values.get(1));
                                                                        vector[1] = Double.parseDouble(values.get(2));
                                                                        vector[2] = Double.parseDouble(values.get(3));
                                                                        vector[3] = Double.parseDouble(values.get(4));

                                                                        System.arraycopy(vector, 0, features, 0, vector.length - 1);


                                                                        GaussianTreeNode profiler = (GaussianTreeNode) context.variable("profiler");
                                                                        profiler.internalLearn(vector, features, new Callback<Boolean>() {
                                                                            @Override
                                                                            public void on(Boolean result) {
                                                                                long c = counter.addAndGet(1);
                                                                                if (c % 10000 == 0) {
                                                                                    long end = System.nanoTime();
                                                                                    double time = end - starttime.get();
                                                                                    time = time / 1000000000;
                                                                                    time = c / time;
                                                                                    double d = c;
                                                                                    d = d / 1000000;
                                                                                    System.out.println(d + " m @ " + time + " values/sec, counter: " + c);
                                                                                }
                                                                                context.continueTask();
                                                                            }
                                                                        });

                                                              /*  long c = counter.addAndGet(1);
                                                                if (c % 1000 == 0) {
                                                                    long end = System.nanoTime();
                                                                    double time = end - starttime.get();
                                                                    time = time / 1000000000;
                                                                    time = c / time;
                                                                    double d = c;
                                                                    d = d / 1000000;
                                                                    System.out.println(d + " m @ " + time + " values/sec");
                                                                }*/

                                                                        //TODO lookup and train

                                                                        //end


                                                                    }
                                                                })
                                                )
                                ));
                t.executeWith(g, null, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        System.out.println("end!");
                        result.free();
                        waiter.count();
                    }
                });
            }
        });

        try {
            waiter.waitResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
