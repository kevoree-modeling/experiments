package org.mwg.experiments.smartgridprofiling.utility;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution;
import org.mwg.struct.Matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by assaad on 31/05/16.
 */
public class AllUserTraining {
    public static void main(String[] arg) {
        String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

        final Graph graph = new org.mwg.GraphBuilder()
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .withMemorySize(10_000_000)
//                .saveEvery(10_000)
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
                    double[][] sumError = new double[1][2];
                    double[] min, max, xArray, yArray;
                    double xrange, yrange;
                    xConfig[2] = 48;
                    yConfig[2] = 100;

                    final long[] accumulator = new long[1];


                    final int MAXLEVEL = 3;
                    final int WIDTH = 60;
                    final double FACTOR = 1.5;
                    final int ITER = 40;
                    final double THRESHOLD = 1.5;
                    double time = 0;



                    //Loading the training set
                    File dir = new File(csvdir + "users/");
                    File[] directoryListing = dir.listFiles();

                    Matrix covBackup =  VolatileMatrix.wrap(null, 2, 2);
                    for (int i = 0; i < 2; i++) {
                        covBackup.set(i, i, err[i]);
                    }
                    MultivariateNormalDistribution mvnBackup = new MultivariateNormalDistribution(null, covBackup, false);


                    if (directoryListing != null) {
                        for (File file : directoryListing) {
                            if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                                continue;
                            }
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            username = file.getName().split("\\.")[0];
                            //   Node smartmeter = graph.newNode(0, 0);
                            final GaussianMixtureNode profiler = (GaussianMixtureNode) graph.newTypedNode(0, 0, GaussianMixtureNode.NAME);
                            profiler.set(GaussianMixtureNode.LEVEL, Type.INT, MAXLEVEL); //max levels allowed
                            profiler.set(GaussianMixtureNode.WIDTH, Type.INT, WIDTH); //each level can have 48 components
                            profiler.set(GaussianMixtureNode.COMPRESSION_FACTOR, Type.DOUBLE, FACTOR); //Factor of times before compressing, so at 24x10=240, compressions executes
                            profiler.set(GaussianMixtureNode.COMPRESSION_ITER, Type.INT, ITER); //iteration in the compression function, keep default
                            profiler.set(GaussianMixtureNode.THRESHOLD, Type.DOUBLE, THRESHOLD); //At the lower level, at higher level will be: threashold + level/2 -> number of variance tolerated to insert in the same node
                            profiler.set(GaussianMixtureNode.PRECISION, Type.DOUBLE_ARRAY, err); //Minimum covariance in both axis


                            //   smartmeter.set("name", Type.STRING, username);
                            //  smartmeter.addToRelation("profile", profiler);

                            profiler.set("name", Type.STRING, username);

                            //  graph.index("nodes", smartmeter, "name", null);

                            graph.index(0, 0, "profilers", new Callback<NodeIndex>() {
                                @Override
                                public void on(NodeIndex result) {
                                    result.addToIndex(profiler,"name");
                                }
                            });



                            ArrayList<double[]> vecs = new ArrayList<double[]>();


                        /*    while ((line[0] = br.readLine()) != null) {
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
//                                            result.rel("profile", (profilers) -> {
//                                                long s = System.nanoTime();
//                                                ((GaussianSlotNode) profilers[0]).learnArray(new double[]{ElectricMeasure.convertTime(timestamp[0]), pv});
//                                                long t = System.nanoTime();
//                                                accumulator[0] += (t - s);
//                                                profilers[0].free();
//                                            });
                                            result.free();
                                        }
                                    });

                                    double[] vec = new double[]{ElectricMeasure.convertTime(timestamp[0]), pv};
                                    vecs.add(vec);

                                    long s = System.nanoTime();
                                    profiler.learnVector(vec, result1 -> {
                                    });
                                    long t = System.nanoTime();
                                    accumulator[0] += (t - s);
                                    globaltotal[0]++;

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                            min = profiler.getMin();
                            max = profiler.getMax();
                            xConfig[0] = min[0];
                            yConfig[0] = min[1];
                            xConfig[1] = max[0];
                            yConfig[1] = max[1];

                            // first create a 100x100 grid
                            xArray = new double[(int) xConfig[2] + 1];
                            yArray = new double[(int) yConfig[2] + 1];

                            yrange = (yConfig[1] - yConfig[0]);
                            yrange = yrange / yConfig[2];

                            xrange = (xConfig[1] - xConfig[0]);
                            xrange = xrange / xConfig[2];

                            for (int i = 0; i < yArray.length; i++) {
                                yArray[i] = i * yrange + yConfig[0];
                            }
                            for (int i = 0; i < xArray.length; i++) {
                                xArray[i] = i * xrange + xConfig[0];
                            }

                            double[][] featArray = new double[(xArray.length * yArray.length)][2];
                            int count = 0;
                            for (int i = 0; i < xArray.length; i++) {
                                for (int j = 0; j < yArray.length; j++) {
                                    double[] point = {xArray[i], yArray[j]};
                                    featArray[count] = point;
                                    count++;
                                }
                            }

                            MultivariateNormalDistribution[] distributions = new MultivariateNormalDistribution[vecs.size()];
                            for (int i = 0; i < vecs.size(); i++) {
                                distributions[i] = mvnBackup.clone(vecs.get(i));
                            }


                            final ProbaDistribution probaAll = new ProbaDistribution(null, distributions, vecs.size());
                            profiler.query(0, null, null, new Callback<ProbaDistribution>() {
                                @Override
                                public void on(ProbaDistribution result) {
                                    double[] ress = result.compareProbaDistribution(probaAll, featArray);
                                    sumError[0][0] += ress[0];
                                    if (ress[1] > sumError[0][1]) {
                                        sumError[0][1] = ress[1];
                                    }

                                }
                            });*/

                            profiler.free();


                            nuser++;
                            graph.save(null);
                            time = accumulator[0] / 1000000000.0;

                            br.close();


                            //  System.out.println("File " + file.getName() + " parsed successfully");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        graph.save(null);
        graph.disconnect(null);
    }
}
