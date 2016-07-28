package org.mwg.experiments.kdtree;

import org.junit.Test;
import org.mwg.*;
import org.mwg.core.scheduler.ExecutorScheduler;
import org.mwg.core.scheduler.HybridScheduler;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.core.scheduler.TrampolineScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.common.distance.EuclideanDistance;
import org.mwg.ml.common.structure.KDNode;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import static org.mwg.task.Actions.repeat;
import static org.mwg.task.Actions.then;


/**
 * Created by assaad on 30/06/16.
 */
public class KDNodeTest {
    @Test
    public void KDInsertTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin())
                .withScheduler(new TrampolineScheduler())
                .withMemorySize(5000000)
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                KDNode test = (KDNode) graph.newTypedNode(0, 0, KDNode.NAME);
                test.set(KDNode.DISTANCE_THRESHOLD, 1e-30);


                int dim = 4;
                double[] vec = new double[dim];
                Random rand = new Random(1569358742365l);
                int num = 100000;
                graph.save(null);
                ArrayList<double[]> vecs = new ArrayList<double[]>();

                final long[] ts = {System.nanoTime()};

                DeferCounter dc = graph.newCounter(num);
                for(int i=0;i<num;i++){
                    double[] valuecop=new double[vec.length];
                    for(int j=0;j<dim;j++){
                        vec[j]=rand.nextDouble();
                        valuecop[j]=vec[j];
                    }
                    vecs.add(vec);

                    Node value= graph.newNode(0,0);
                    value.set("value",valuecop);

                    test.insert(vec, value, new Callback<Boolean>() {
                        @Override
                        public void on(Boolean result) {
                            value.free();
                            dc.count();
                        }
                    });
                }

                dc.then(new Job() {
                    @Override
                    public void run() {
                        long tf=System.nanoTime();
                        double time=tf-ts[0];
                        time=time/1000000;
                        System.out.println("learning in "+time+" ms");
                        double speed=num*1000;
                        speed=speed/time;
                        System.out.println("Speed: "+speed+" v/s");

                        graph.save(null);
                        System.out.println(num+", cache: "+graph.space().available()+", nodes: "+test.get(KDNode.NUM_NODES));

                    /*    double[] key=new double[dim];
                        for(int i=0;i<dim;i++){
                            key[i]=0.1*(i+1);
                        }

                        NumberFormat formatter = new DecimalFormat("#0.0000");
                        System.out.println();
                        test.nearestN(key, 8, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result) {
                                for(int i=0;i<result.length;i++){
                                    double[] vec=(double[]) result[i].get("value");
                                    for(int j=0;j<vec.length;j++){
                                        System.out.print(formatter.format(vec[j])+" ");
                                    }
                                    System.out.println("dist: " + formatter.format(new EuclideanDistance().measure(vec,key)));
                                    result[i].free();
                                }
                            }
                        });
                        graph.save(null);
                        System.out.println("cache: "+graph.space().available());


                        EuclideanDistance ed =new EuclideanDistance();
                        double[] sum=new double[1];
                        sum[0]=0;


                        ts[0]=System.nanoTime();
                        for(int i=0;i<vecs.size();i++){
                            double[] v1=vecs.get(i);
                            test.nearestN(v1, 1, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    for(int i=0;i<result.length;i++){
                                        double[] vect=(double[]) result[i].get("value");
                                        double v3= ed.measure(v1,vect);
                                        sum[0]+=v3;
                                    }
                                }
                            });


                        }
                        tf=System.nanoTime();
                        time=tf-ts[0];
                        time=time/1000000;
                        System.out.println("Sum: "+sum[0]+" in "+time+" ms");*/
                    }
                });



         /*       repeat(num+"" , then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        double[] valuecop = new double[vec.length];
                        for (int j = 0; j < dim; j++) {
                            vec[j] = rand.nextDouble();
                            valuecop[j] = vec[j];
                        }
                        vecs.add(vec);

                        Node value = graph.newNode(0, 0);
                        value.set("value", valuecop);

                        test.insert(vec, value, new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {
                                value.free();
                                context.continueTask();
                                xx[0]++;
                                if(xx[0]>99990) {
                                    System.out.println(xx[0] +"ss");
                                }
                            }
                        });
                    }
                })).execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {


                        long tf = System.nanoTime();
                        double time = tf - ts[0];
                        result.free();

                        time = time / 1000000;
                        System.out.println("learning in " + time + " ms");
                        double speed = num * 1000;
                        speed = speed / time;
                        System.out.println("Speed: " + speed + " v/s");

                        graph.save(null);
                        System.out.println(num + ", cache: " + graph.space().available() + ", nodes: " + test.get(KDNode.NUM_NODES));

                        double[] key = new double[dim];
                        for (int i = 0; i < dim; i++) {
                            key[i] = 0.1 * (i + 1);
                        }

                        NumberFormat formatter = new DecimalFormat("#0.0000");
                        System.out.println();
                        test.nearestN(key, 8, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result) {
                                for (int i = 0; i < result.length; i++) {
                                    double[] vec = (double[]) result[i].get("value");
                                    for (int j = 0; j < vec.length; j++) {
                                        System.out.print(formatter.format(vec[j]) + " ");
                                    }
                                    System.out.println("dist: " + formatter.format(new EuclideanDistance().measure(vec, key)));
                                    result[i].free();
                                }
                            }
                        });
                        graph.save(null);
                        System.out.println("cache: " + graph.space().available());


                        EuclideanDistance ed = new EuclideanDistance();
                        double[] sum = new double[1];
                        sum[0] = 0;


                        ts[0] = System.nanoTime();
                        for (int i = 0; i < vecs.size(); i++) {
                            double[] v1 = vecs.get(i);
                            test.nearestN(v1, 1, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    for (int i = 0; i < result.length; i++) {
                                        double[] vect = (double[]) result[i].get("value");
                                        double v3 = ed.measure(v1, vect);
                                        sum[0] += v3;
                                    }
                                }
                            });


                        }
                        tf = System.nanoTime();
                        time = tf - ts[0];
                        time = time / 1000000;
                        System.out.println("Sum: " + sum[0] + " in " + time + " ms");
                    }
                });*/

            }
        });

    }
}