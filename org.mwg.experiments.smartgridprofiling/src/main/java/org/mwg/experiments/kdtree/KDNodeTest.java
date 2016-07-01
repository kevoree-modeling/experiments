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
                .withMemorySize(100000)
                .withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                KDNode test= (KDNode) graph.newTypedNode(0,0,KDNode.NAME);
                test.set(KDNode.DISTANCE_THRESHOLD,1e-30);


                int dim=3;
                double[] vec=new double[dim];
                Random rand=new Random();
                int num=1000;
                graph.save(null);

                for(int i=0;i<num;i++){
                    double[] valuecop=new double[vec.length];
                    for(int j=0;j<dim;j++){
                        vec[j]=rand.nextDouble();
                        valuecop[j]=vec[j];
                    }

                    Node value= graph.newNode(0,0);
                    value.set("value",valuecop);

                    test.insert(vec,value,null);
                    value.free();
                    if(i%10000==0) {
                        graph.save(null);
                        System.out.println(i+", cache: "+graph.space().available()+", nodes: "+test.get(KDNode.NUM_NODES));
                    }
                }

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




            }
        });
    }
}