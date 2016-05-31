package org.mwg.experiments.smartgridprofiling.gmm;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.algorithm.profiling.GaussianGmmNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by assaad on 31/05/16.
 */
public class AllUserTraining {
    public static void main(String[] arg) {
        String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

        final Graph graph = org.mwg.GraphBuilder.builder()
                .withFactory(new GaussianGmmNode.Factory())
                .withScheduler(new NoopScheduler())
                // .withOffHeapMemory()
                .withMemorySize(1_000_000)
                .withAutoSave(10_000)
                .withStorage(new LevelDBStorage(csvdir))
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
                    final int MAXLEVEL = 4;
                    final double[] err = new double[]{0.25 * 0.25, 10 * 10};

                    long minTraining = Long.MAX_VALUE;
                    long maxTraining = Long.MIN_VALUE;
                    final long[] maxTesting = {Long.MIN_VALUE};

                    final long[] accumulator = new long[1];


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
                            final GaussianGmmNode profiler = (GaussianGmmNode) graph.newTypedNode(0, 0, GaussianGmmNode.NAME);
                            profiler.set(GaussianGmmNode.LEVEL_KEY, MAXLEVEL); //max levels allowed
                            profiler.set(GaussianGmmNode.WIDTH_KEY, 100); //each level can have 24 components
                            profiler.set(GaussianGmmNode.COMPRESSION_FACTOR_KEY, 4); //Factor of times before compressing, so at 24x10=240, compressions executes
                            profiler.set(GaussianGmmNode.COMPRESSION_ITER_KEY, 5); //iteration in the compression function, keep default
                            profiler.set(GaussianGmmNode.THRESHOLD_KEY, 1.0); //At the lower level, at higher level will be: threashold + level/2 -> number of variance tolerated to insert in the same node
                            profiler.set(GaussianGmmNode.PRECISION_KEY, err); //Minimum covariance in both axis


                            smartmeter.set("name", username);
                            smartmeter.add("profile", profiler);
                            graph.index("nodes", smartmeter, "name", null);

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
                                    smartmeter.jump(timestamp[0], new Callback<Node>() {
                                        @Override
                                        public void on(Node result) {
                                            result.set("power", pv);
//                                            result.rel("profile", (profilers) -> {
//                                                long s = System.nanoTime();
//                                                ((GaussianSlotProfilingNode) profilers[0]).learnArray(new double[]{ElectricMeasure.convertTime(timestamp[0]), pv});
//                                                long t = System.nanoTime();
//                                                accumulator[0] += (t - s);
//                                                profilers[0].free();
//                                            });
                                            result.free();
                                        }
                                    });

                                    long s = System.nanoTime();
                                    profiler.learnVector(new double[]{ElectricMeasure.convertTime(timestamp[0]), pv}, new Callback<Boolean>() {
                                        @Override
                                        public void on(Boolean result) {

                                        }
                                    });
                                    long t = System.nanoTime();
                                    accumulator[0] += (t - s);
                                    globaltotal[0]++;
                                    if (globaltotal[0] % 10000 == 0) {
                                        long endtime = System.nanoTime();
                                        double restime = (globaltotal[0]) / ((endtime - starttime) / 1000000.0);
                                        graph.save(null);
                                        System.out.println("Loaded " + globaltotal[0] / 1000000.0 + " m power records in " + restime + " kv/s users " + nuser + " cache size " + graph.space().available());
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }


                            smartmeter.free();
                            profiler.free();
                            nuser++;
//                            if (nuser % 10 == 0) {
//                                System.out.println(nuser+" "+globaltotal);
//                            }
                            br.close();
                            //  System.out.println("File " + file.getName() + " parsed successfully");
                        }
                    }


                    final long[] endtime = {System.nanoTime()};
                    final double[] restime = {(endtime[0] - starttime) / 1000000000};
                    System.out.println("Loaded " + globaltotal[0] + " power records in " + restime[0] + " s !");
                    System.out.println("Profiling took: " + accumulator[0] + " ns");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
