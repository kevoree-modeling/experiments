package org.mwg.experiments.smartgridprofiling;

import org.mwg.*;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by assaad on 26/10/2016.
 */
public class PaperExperiment {
    final static int SLOTS = 12;
    final static String csvdir = "/Users/assaad/Documents/datasets/";

    public static void main(String[] arg) {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin())
//                .withOffHeapMemory()
                .withMemorySize(10_000_000)
//                .saveEvery(1000)
                .withStorage(new LevelDBStorage(csvdir + "leveldb/"))
                .build();
        graph.connect(new Callback<Boolean>() {

            public void on(Boolean result) {

                System.out.println("Available space initially: " + graph.space().available());

                final int[] globaltotal = {0};
                long starttime = System.nanoTime();
                try {
                    final String[] line = {""};
                    int nuser = 0;
                    String cvsSplitBy = ",";
                    final int[] powerValue = new int[1];
                    String username;
                    final String[][] splitted = new String[1][1];
                    final long[] timestamp = new long[1];

                    long minTraining = Long.MAX_VALUE;
                    long maxTraining = Long.MIN_VALUE;

                    final long[] accumulator = new long[2];
                    long halfHour = 1800 * 1000;
                    int MAXCONC = 2;
                    int MAXUSER = 300;

                    //Two main concentrator nodes
                    Node[] concentrator = new Node[MAXCONC];
                    Node[] profiles = new Node[MAXUSER];
                    Node[] users = new Node[MAXUSER];
                    int iconc = 0;
                    int connections = 0;


                    //Loading the training set
                    File dir = new File(csvdir + "training300/");
                    File[] directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        for (File file : directoryListing) {
                            if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                                continue;
                            }
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            username = file.getName().split("\\.")[0];
                            Node smartmeter = graph.newNode(0, 0);
                            final Node profiler = graph.newTypedNode(0, 0, GaussianSlotNode.NAME);
                            profiler.set(GaussianSlotNode.SLOTS_NUMBER, SLOTS); //one slot every hour
                            smartmeter.set("name", username);
                            smartmeter.add("profile", profiler);
                            graph.index("nodes", smartmeter, "name", null);

                            //create the concentrator if null
                            if (concentrator[iconc] == null) {
                                concentrator[iconc] = graph.newNode(0, 0);
                            }

                            //add the current smart meter
                            concentrator[iconc].add("smartmeters", smartmeter);
                            connections++;

                            //if connections are full, increase concentrators
                            if (connections == 150) {
                                iconc++;
                                connections = 0;
                            }

                            //Fill electrical loads of one meter
                            while ((line[0] = br.readLine()) != null) {
                                try {

                                    splitted[0] = line[0].split(cvsSplitBy);
                                    if (splitted[0].length != 2) {
                                        continue;
                                    }
                                    timestamp[0] = Long.parseLong(splitted[0][0]);
                                    powerValue[0] = Integer.parseInt(splitted[0][1]);
                                    if (timestamp[0] < minTraining) {
                                        minTraining = timestamp[0];
                                    }
                                    if (timestamp[0] > maxTraining) {
                                        maxTraining = timestamp[0];
                                    }
                                    final int pv = powerValue[0];


                                    long s = System.nanoTime();
                                    smartmeter.jump(timestamp[0], new Callback<Node>() {
                                        @Override
                                        public void on(Node result) {
                                            result.set("power", pv);

                                            result.rel("profile", (profilers) -> {
                                                long s = System.nanoTime();
                                                ((GaussianSlotNode) profilers[0]).learnArray(new double[]{pv});
                                                long t = System.nanoTime();
                                                accumulator[0] += (t - s);
                                                profilers[0].free();
                                            });

                                            result.free();
                                        }
                                    });
                                    long t = System.nanoTime();
                                    accumulator[1] += (t - s);

                                    globaltotal[0]++;
                                    if (globaltotal[0] % 1000000 == 0) {
                                        long endtime = System.nanoTime();
                                        double restime = (globaltotal[0]) / ((endtime - starttime) / 1000000.0);
                                        graph.save(null);
                                        System.out.println("Loaded " + globaltotal[0] / 1000000.0 + " m power records in " + restime + " kv/s users " + nuser + " space: " + graph.space().available());
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            profiles[nuser] = profiler;
                            users[nuser] = smartmeter;
                            nuser++;
//                            if (nuser % 1000 == 0) {
//                                System.out.println(nuser + " " + globaltotal);
//                            }
                            br.close();
                            //  System.out.println("File " + file.getName() + " parsed successfully");
                        }
                    }


                    final long[] endtime = {System.nanoTime()};
                    final double[] restime = {(endtime[0] - starttime)};
                    accumulator[1] = (accumulator[1] - accumulator[0]);


                    System.out.println();
                    System.out.println("Loaded " + globaltotal[0] + " power records in " + restime[0] + " ns !");
                    System.out.println();
                    System.out.println("Profiling took: " + accumulator[0] + " ns");
                    System.out.println("Lookup took: " + accumulator[1] + " ns");
                    System.out.println("Management took: " + (restime[0] - accumulator[1] - accumulator[0]) + " ns");
                    System.out.println();


                    double speed = (1000000.0 * globaltotal[0]) / accumulator[0];

                    System.out.println("profiling time: " + (accumulator[0] / globaltotal[0]) + " ns/v");
                    System.out.println("lookup time: " + (accumulator[1] / globaltotal[0]) + " ns/v");
                    System.out.println();


                    System.out.println("profiling speed: "+((globaltotal[0] * 1000000.0) / accumulator[0])+" kv/s");
                    System.out.println("Lookup speed: "+((globaltotal[0] * 1000000.0) / accumulator[1])+" kv/s");
                    System.out.println("Learning speed: "+((globaltotal[0] * 1000000.0) / (accumulator[1]+accumulator[0]))+" kv/s");

                    System.out.println();
                    double overallspeed = (globaltotal[0] * 1000000.0) / restime[0];
                    System.out.println("Overall speed: " + overallspeed + " kv/s");
                    System.out.println();



                    long[] countnull = new long[2];
                    long profileTime = System.nanoTime();
                    long[] predtt=new long[2];
                    int totProf = 0;
                    for (int i = 0; i < MAXUSER; i++) {
                        double[] pp = new double[1];

                        for (long time = maxTraining; time < maxTraining + 48 * halfHour; time += halfHour) {
                            totProf++;
                            graph.lookup(0, time, users[i].id(), new Callback<Node>() {
                                @Override
                                public void on(Node result) {
                                    result.rel("profile", new Callback<Node[]>() {
                                        @Override
                                        public void on(Node[] result) {
                                            long s = System.nanoTime();
                                            ((GaussianSlotNode) result[0]).predict(new Callback<double[]>() {
                                                @Override
                                                public void on(double[] result) {
                                                    if (result == null) {
                                                        countnull[0]++;
                                                    } else {
                                                        countnull[1]++;
                                                        pp[0] += result[0];
                                                    }
                                                }
                                            });
                                            long t = System.nanoTime();
                                            predtt[0]+=(t-s);
                                            result[0].free();
                                        }
                                    });
                                    result.free();
                                }
                            });
                        }
                        pp[0] = pp[0] / 48;
                    }
                    profileTime = System.nanoTime() - profileTime;
                    System.out.println("Only prediction: "+predtt[0]+" ns");

                    System.out.println("Profile exec time per user per time, in ns: " + profileTime + " total calls: " + totProf + " should divide ");
                    System.out.println("Only pred speed: "+(totProf*1000000.0)/predtt[0]+" kv/s");
                    System.out.println("Overall pred speed: "+(totProf*1000000.0)/profileTime+" kv/s");
                    System.out.println("profile null " + countnull[0] + " non null " + countnull[1]);







                    clean(concentrator, profiles, users, graph);


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private static void clean(Node[] concentrator, Node[] profiles, Node[] users, Graph graph) {
        for (int i = 0; i < concentrator.length; i++) {
            if (concentrator[i] != null) {
                concentrator[i].free();
            }
        }

        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i] != null) {
                profiles[i].free();
            }
        }
        for (int i = 0; i < users.length; i++) {
            if (users[i] != null) {
                users[i].free();
            }
        }
        graph.save(null);
        System.out.println("available space: " + graph.space().available());
    }
}
