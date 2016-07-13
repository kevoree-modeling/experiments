package org.mwg.experiments.kdtree;

import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.common.distance.EuclideanDistance;
import org.mwg.ml.common.structure.KDNode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by assaad on 30/06/16.
 */
public class KDNodeTest {
    @Test
    public void KDInsertTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .withMemorySize(1000000)
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                KDNode test= (KDNode) graph.newTypedNode(0,0,KDNode.NAME);
                test.set(KDNode.DISTANCE_THRESHOLD,1e-30);


                int dim=4;
                double[] vec=new double[dim];
                Random rand=new Random(1569358742365l);
                int num=100000;
                graph.save(null);
                ArrayList<double[]> vecs=new ArrayList<double[]>();

                long ts=System.nanoTime();
                for(int i=0;i<num;i++){
                    double[] valuecop=new double[vec.length];
                    for(int j=0;j<dim;j++){
                        vec[j]=rand.nextDouble();
                        valuecop[j]=vec[j];
                    }
                    vecs.add(vec);

                    Node value= graph.newNode(0,0);
                    value.set("value",valuecop);

                    test.insert(vec,value,null);
                    value.free();
                   /* if(i%10000==0) {
                        graph.save(null);
                        System.out.println(i+", cache: "+graph.space().available()+", nodes: "+test.get(KDNode.NUM_NODES));
                    }*/
                }
                long tf=System.nanoTime();
                double time=tf-ts;
                time=time/1000000;
                System.out.println("learning in "+time+" ms");

                graph.save(null);
                System.out.println(num+", cache: "+graph.space().available()+", nodes: "+test.get(KDNode.NUM_NODES));

                double[] key=new double[dim];
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
                        }
                    }
                });
                graph.save(null);
                System.out.println("cache: "+graph.space().available());


                EuclideanDistance ed =new EuclideanDistance();
                double[] sum=new double[1];
                sum[0]=0;


                ts=System.nanoTime();
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
                time=tf-ts;
                time=time/1000000;
                System.out.println("Sum: "+sum[0]+" in "+time+" ms");



            }
        });
    }
}