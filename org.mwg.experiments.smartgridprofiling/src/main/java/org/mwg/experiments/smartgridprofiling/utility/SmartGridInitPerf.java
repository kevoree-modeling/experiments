package org.mwg.experiments.smartgridprofiling.utility;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by assaad on 27/04/16.
 */
public class SmartGridInitPerf {
    final static int SLOTS = 12;
    final static String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
    //final static String csvdir = "/Users/duke/Desktop/londonpower/";

    public static void main(String[] arg) {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
//                .withOffHeapMemory()
                .withMemorySize(1_000_000)
//                .saveEvery(10_000)
                .withStorage(new RocksDBStorage(csvdir + "rocksdb/"))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {


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

                    PrintWriter out = new PrintWriter(new File(csvdir + "perf.csv"));
                    final long[] maxTesting = {Long.MIN_VALUE};

                    final long[] accumulator = new long[1];

                    //Two main concentrator nodes
                    Node concentrator = graph.newNode(0, 0);
                    Node backup = graph.newNode(0, 0);
                    int connections = 0;


                    //Loading the training set
                    File dir = new File(csvdir + "users/");
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

                            if (connections < 100) {
                                concentrator.add("smartmeters", smartmeter);
                                connections++;
                            } else {
                                backup.add("smartmeters", smartmeter);
                            }
                            while ((line[0] = br.readLine()) != null) {
                                try {

                                    splitted[0] = line[0].split(cvsSplitBy);
                                    if (splitted[0].length != 2) {
                                        continue;
                                    }
                                    timestamp[0] = Long.parseLong(splitted[0][0]);
                                    powerValue[0] = Integer.parseInt(splitted[0][1]);

                                    final int pv = powerValue[0];
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
                                    globaltotal[0]++;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }


                            smartmeter.free();
                            profiler.free();
                            nuser++;
                            if (nuser % 10 == 0) {
                                long tt = System.nanoTime() - starttime;
                                out.println(nuser + " , " + globaltotal[0] + " , " + tt +" , " + accumulator[0]);
                                System.out.println(nuser + " , " + globaltotal[0]  + " , " + tt/1000000000  + " , " + accumulator[0]/1000000000);
                                out.flush();
                            }
                            br.close();
                            //  System.out.println("File " + file.getName() + " parsed successfully");
                        }
                    }


                    final long[] endtime = {System.nanoTime()};
                    final double[] restime = {(endtime[0] - starttime) / 1000000000};
                    System.out.println("Loaded " + globaltotal[0] + " power records in " + restime[0] + " s !");
                    System.out.println("Profiling took: " + accumulator[0] + " ns");
                    out.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        System.out.println("test done");


    }

    public static long[] shuffle(long[] ids, Random rand) {
        for (int i = ids.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            long temp = ids[i];
            ids[i] = ids[j];
            ids[j] = temp;
        }
        return ids;
    }
}
