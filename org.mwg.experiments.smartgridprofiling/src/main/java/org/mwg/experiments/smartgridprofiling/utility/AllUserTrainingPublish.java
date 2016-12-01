package org.mwg.experiments.smartgridprofiling.utility;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.experiments.smartgridprofiling.gmm.ElectricMeasure;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by assaad on 31/05/16.
 */
public class AllUserTrainingPublish {
    public static void main(String[] arg) {
        String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

        final Graph graph = new org.mwg.GraphBuilder()
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
//                .withOffHeapMemory()
//                .withMemorySize(1_000_000)
//                .saveEvery(1_000)
                .withStorage(new LevelDBStorage(csvdir).useNative(false))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {


                final long[] globaltotal = {0};
                try {

                    final String[] line = {""};
                    int nuser = 0;
                    String cvsSplitBy = ",";
                    final int[] powerValue = new int[1];
                    String username;
                    final String[][] splitted = new String[1][1];
                    final long[] timestamp = new long[1];
                    final double[] err = new double[]{0.25 * 0.25, 10 * 10};

                    long minTraining = Long.MAX_VALUE;
                    long maxTraining = Long.MIN_VALUE;
                    double[] xConfig = new double[3];
                    double[] yConfig = new double[3];
                    xConfig[2] = 48;
                    yConfig[2] = 100;

                    final long[] accumulator = new long[1];


                    final int MAXLEVEL = 3;
                    final int WIDTH = 50;
                    final double FACTOR = 1.8;
                    final int ITER = 20;
                    final double THRESHOLD = 1.6;

                 //   PrintWriter out = new PrintWriter(new File(csvdir + "RESULT-L" + MAXLEVEL + "-W" + WIDTH + "-F" + FACTOR + "-I" + ITER + "-T" + THRESHOLD + ".csv"));
                   // PrintWriter pw = new PrintWriter(new FileOutputStream(new File(csvdir + "FINAL.csv"), true));

                    //Loading the training set
                    File dir = new File(csvdir + "training300/");
                    File[] directoryListing = dir.listFiles();

                    GaussianMixtureNode globalProfile=(GaussianMixtureNode)graph.newTypedNode(0,0,GaussianMixtureNode.NAME);
                    globalProfile.set("name","GLOBAL");
                    globalProfile.set(GaussianMixtureNode.LEVEL, MAXLEVEL); //max levels allowed
                    globalProfile.set(GaussianMixtureNode.WIDTH, WIDTH); //each level can have 48 components
                    globalProfile.set(GaussianMixtureNode.COMPRESSION_FACTOR, FACTOR); //Factor of times before compressing, so at 24x10=240, compressions executes
                    globalProfile.set(GaussianMixtureNode.COMPRESSION_ITER, ITER); //iteration in the compression function, keep default
                    globalProfile.set(GaussianMixtureNode.THRESHOLD, THRESHOLD); //At the lower level, at higher level will be: threashold + level/2 -> number of variance tolerated to insert in the same node
                    globalProfile.set(GaussianMixtureNode.PRECISION, err); //Minimum covariance in both axis





                    graph.index("profilers", globalProfile, "name", null);

                    if (directoryListing != null) {
                        for (File file : directoryListing) {
                            if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                                continue;
                            }
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            username = file.getName().split("\\.")[0];
                            Node smartmeter = graph.newNode(0, 0);
                            final GaussianMixtureNode profiler = (GaussianMixtureNode) graph.newTypedNode(0, 0, GaussianMixtureNode.NAME);
                            profiler.set(GaussianMixtureNode.LEVEL, Type.INT, MAXLEVEL); //max levels allowed
                            profiler.set(GaussianMixtureNode.WIDTH, Type.INT, WIDTH); //each level can have 48 components
                            profiler.set(GaussianMixtureNode.COMPRESSION_FACTOR, Type.DOUBLE, FACTOR); //Factor of times before compressing, so at 24x10=240, compressions executes
                            profiler.set(GaussianMixtureNode.COMPRESSION_ITER, Type.INT, ITER); //iteration in the compression function, keep default
                            profiler.set(GaussianMixtureNode.THRESHOLD, Type.DOUBLE, THRESHOLD); //At the lower level, at higher level will be: threashold + level/2 -> number of variance tolerated to insert in the same node
                            profiler.set(GaussianMixtureNode.PRECISION, Type.DOUBLE_ARRAY, err); //Minimum covariance in both axis


                            smartmeter.set("name", username);
                            smartmeter.addToRelation("profile", profiler);

                            profiler.set("name", username);
                            graph.index("nodes", smartmeter, "name", null);
                            graph.index("profilers", profiler, "name", null);

                            ArrayList<double[]> vecs = new ArrayList<double[]>();


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
                                    smartmeter.travelInTime(timestamp[0], new Callback<Node>() {
                                        @Override
                                        public void on(Node result) {
                                            result.set("power", Type.DOUBLE, pv);
                                            result.free();
                                        }
                                    });

                                    double[] vec = new double[]{ElectricMeasure.convertTime(timestamp[0]), pv};
                                    vecs.add(vec);

                                    long s = System.nanoTime();
                                    profiler.learnVector(vec, result1 -> {});
                                    long t = System.nanoTime();
                                    globalProfile.learnVector(vec,result1 -> {});
                                    accumulator[0] += (t - s);
                                    globaltotal[0]++;

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }


                            profiler.free();


                            nuser++;
                            graph.save(null);
                            br.close();
                            System.out.println(nuser+", "+globaltotal[0]);


                        }
                    }

                    System.out.println("Loaded " + globaltotal[0] + " power records ");
                    System.out.println("Profiling took: " + accumulator[0] + " ns");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        graph.save(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                graph.disconnect(null);
            }
        });
    }
}
