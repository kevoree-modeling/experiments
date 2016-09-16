package org.mwg.experiments.mwgrelated;

import org.junit.Assert;
import org.mwg.*;
import org.mwg.structure.KDTreeJava;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.distance.EuclideanDistance;
import org.mwg.structure.distance.GeoDistance;
import org.mwg.structure.tree.KDTree;
import org.mwg.structure.tree.NDTree;
import org.mwg.structure.tree.NDTree2;

import java.util.Random;

/**
 * Created by assaad on 16/09/16.
 */
public class TestGraph {
    public static void main(String[] arg){

        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .withMemorySize(10000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NDTree ndTree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);
                KDTree kdtree = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);
                NDTree2 ndTree2 = (NDTree2) graph.newTypedNode(0, 0, NDTree2.NAME);


                Node root = graph.newNode(0,0);
                root.setProperty("name", Type.STRING,"root");
                root.add("kdtree",kdtree);
                root.add("kdtree",ndTree);
                root.add("kdtree",ndTree2);
                graph.index("KDTREE",root,"name",null);



                KDTreeJava kdtreejava = new KDTreeJava();
                kdtreejava.setThreshold(KDTree.DISTANCE_THRESHOLD_DEF);

                boolean print = true;


                kdtreejava.setDistance(GeoDistance.instance());
                kdtree.setDistance(Distances.GEODISTANCE);
                ndTree.setDistance(Distances.GEODISTANCE);
                ndTree2.setDistance(Distances.GEODISTANCE);

                int dim = 2;

                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];

                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.25;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }


                ndTree.setBounds(boundMin, boundMax, precisions);
                ndTree2.setBounds(boundMin, boundMax);


                Random random = new Random();
                random.setSeed(125362l);
                int ins = 100;

                graph.save(null);
                long initcache = graph.space().available();


                double[][] keys = new double[ins][];
                Node[] values = new Node[ins];


                for (int i = 0; i < ins; i++) {
                    Node temp = graph.newNode(0, 0);
                    //temp.setProperty("value", Type.DOUBLE, random.nextDouble());

                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }

                    temp.set("key", key);
                    keys[i] = key;
                    values[i] = temp;
                }

                for (int i = 0; i < ins; i++) {
                    ndTree.insertWith(keys[i], values[i], null);
                    kdtree.insertWith(keys[i], values[i], null);
                    kdtreejava.insert(keys[i], values[i], null);
                    ndTree2.insertWith(keys[i], values[i], null);
                }


                System.out.println("ndtree: " + ndTree.size());
                System.out.println("ndtree2: " + ndTree2.size());


//                printgraph(ndTree, "ndtree");
//                printgraph(ndTree2, "ndtree2");

                for (int j = 0; j < ins; j++) {

                    final double[] res = keys[j];
                    int finalJ = j;
                    ndTree.nearestN(res, 10, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            ndTree2.nearestN(res, 10, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result2) {

//                                    if(result.length!=result2.length){
//                                        for(int i=0;i<result.length;i++){
//                                            System.out.println("nd " + result[i] + " dist " + GeoDistance.instance().measure(res, (double[]) result[i].get("key")));
//                                        }
//
//                                        System.out.println();
//                                        for(int i=0;i<result2.length;i++){
//                                            System.out.println("nd2 " + result2[i] + " dist " + GeoDistance.instance().measure(res, (double[]) result2[i].get("key")));
//                                        }
//
//                                    }
                                    Assert.assertTrue(result2.length == result.length);


                                    for (int i = 0; i < result.length; i++) {
//                                        if (result[i].id() != result2[i].id()) {
//
//                                            System.out.println("nd " + result[i] + " dist " + GeoDistance.instance().measure(res, (double[]) result[i].get("key")));
//                                            System.out.println("nd2 " + result2[i] + " dist " + GeoDistance.instance().measure(res, (double[]) result2[i].get("key")));
//                                            System.out.println("");
//                                            count[0]++;
//                                            // System.out.println("nd "+result[i].id()+" nd2 "+result2[i].id());
//                                        } else {
//                                            Assert.assertTrue(result[i].id() == result2[i].id());
//                                            count[1]++;
//                                        }
                                        Assert.assertTrue(result[i].id() == result2[i].id());
                                    }



                                }
                            });

                        }
                    });
                }



            }
        });
    }


    public void NDInsertTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                //.withScheduler(new NoopScheduler())
                .withMemorySize(100000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NDTree ndTree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);
                KDTree kdtree = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);

                KDTreeJava kdtreejava = new KDTreeJava();
                kdtreejava.setThreshold(KDTree.DISTANCE_THRESHOLD_DEF);

                boolean print = true;


//                kdtreejava.setDistance(EuclideanDistance.instance());
//                kdtree.setDistance(Distances.EUCLIDEAN);
//                ndTree.setDistance(Distances.EUCLIDEAN);


                kdtreejava.setDistance(GeoDistance.instance());
                kdtree.setDistance(Distances.GEODISTANCE);
                ndTree.setDistance(Distances.GEODISTANCE);

                //Day - Hours - Temperature - Power
             /*   double[] precisions = {1, 0.25, 1, 50};
                double[] boundMin = {0, 0, -10, 0};
                double[] boundMax = {6, 24, 30, 3000};*/

                int dim = 3;

                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];

                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.5;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }

                ndTree.setBounds(boundMin, boundMax, precisions);

                Random random = new Random();
                random.setSeed(125362l);
                int ins = 1000;

                graph.save(null);
                long initcache = graph.space().available();


                double[][] keys = new double[ins][];
                Node[] values = new Node[ins];


                for (int i = 0; i < ins; i++) {
                    Node temp = graph.newNode(0, 0);
                    //temp.setProperty("value", Type.DOUBLE, random.nextDouble());

                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }

                    temp.set("key", key);
                    keys[i] = key;
                    values[i] = temp;
                }

                long starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    ndTree.insertWith(keys[i], values[i], null);
                }
                long endtime = System.currentTimeMillis();
                double exectime = endtime - starttime;
                System.out.println("Nd tree insert: " + exectime + " ms");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdtree.insertWith(keys[i], values[i], null);
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree insert: " + exectime + " ms");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdtreejava.insert(keys[i], values[i], null);
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree java insert: " + exectime + " ms");


//                graph.save(null);
//                Assert.assertTrue(graph.space().available() == initcache);


                double[] res = new double[dim];
                for (int j = 0; j < dim; j++) {
                    res[j] = j * (1.0 / dim);
                }

                System.out.println("ND TREE");

                starttime = System.currentTimeMillis();
                ndTree.nearestN(res, 10, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        if (print) {
                            for (int i = 0; i < result.length; i++) {
                                System.out.println(result[i] + " dist: " + EuclideanDistance.instance().measure(res, (double[]) result[i].get("key")));
                            }
                        }
                    }
                });
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("nd tree search: " + exectime + " ms");


                System.out.println("");
                System.out.println("KD TREE");

                starttime = System.currentTimeMillis();
                kdtree.nearestN(res, 10, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        if (print) {
                            for (int i = 0; i < result.length; i++) {
                                System.out.println(result[i] + " dist: " + EuclideanDistance.instance().measure(res, (double[]) result[i].get("key")));
                            }
                        }

                    }
                });
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree search: " + exectime + " ms");

                System.out.println("");
                System.out.println("KD TREE java");


                starttime = System.currentTimeMillis();
                kdtreejava.nearestN(res, 10, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] result) {
                        if (print) {
                            for (int i = 0; i < result.length; i++) {
                                System.out.println(result[i] + " dist: " + EuclideanDistance.instance().measure(res, (double[]) ((Node) result[i]).get("key")));
                            }
                        }

                    }
                });
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree java search: " + exectime + " ms");
                System.out.println("");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    ndTree.nearestN(keys[i], 10, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                        }
                    });
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("nd tree all search: " + exectime + " ms");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdtree.nearestN(keys[i], 10, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                        }
                    });
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree all search: " + exectime + " ms");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdtreejava.nearestN(keys[i], 10, new Callback<Object[]>() {
                        @Override
                        public void on(Object[] result) {
                        }
                    });
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree java all search: " + exectime + " ms");

            }
        });



    }
}
