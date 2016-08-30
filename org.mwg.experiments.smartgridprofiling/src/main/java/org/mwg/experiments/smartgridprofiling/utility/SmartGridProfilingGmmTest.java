package org.mwg.experiments.smartgridprofiling.utility;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.experiments.smartgridprofiling.gmm.ElectricMeasure;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

/**
 * Created by assaad on 27/04/16.
 */
public class SmartGridProfilingGmmTest {
    final static int SLOTS=24*2;
    final static String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
    //final static String csvdir = "/Users/duke/Desktop/londonpower/";

    public static void main(String[] arg) {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                // .withOffHeapMemory()
                .withMemorySize(1_000_000)
//                .saveEvery(10_000)
                .withStorage(new LevelDBStorage(csvdir + "leveldb/"))
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

                    long minTraining = Long.MAX_VALUE;
                    long maxTraining = Long.MIN_VALUE;
                    final long[] maxTesting = {Long.MIN_VALUE};

                    final long[] accumulator = new long[1];

                    //Two main concentrator nodes
                    Node concentrator = graph.newNode(0, 0);
                    Node backup = graph.newNode(0, 0);
                    int connections = 0;


                    final long[] counts = new long[1];
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
                            final Node profiler = graph.newTypedNode(0, 0, GaussianMixtureNode.NAME);

                            int MAXLEVEL = 1;
                            int WIDTH=50;
                            double FACTOR=1.8;
                            int ITER=20;
                            double THRESHOLD =1.6;

                            profiler.set(GaussianMixtureNode.LEVEL, MAXLEVEL); //max levels allowed
                            profiler.set(GaussianMixtureNode.WIDTH, WIDTH); //each level can have 24 components
                            profiler.set(GaussianMixtureNode.COMPRESSION_FACTOR, FACTOR); //Factor of times before compressing, so at 24x10=240, compressions executes
                            profiler.set(GaussianMixtureNode.COMPRESSION_ITER, ITER); //iteration in the compression function, keep default
                            profiler.set(GaussianMixtureNode.THRESHOLD, THRESHOLD); //At the lower level, at higher level will be: threashold + level/2 -> number of variance tolerated to insert in the same node
                            double[] err = new double[]{0.25 * 0.25, 10 * 10};
                            profiler.set(GaussianMixtureNode.PRECISION, err); //Minimum covariance in both axis

                            smartmeter.set("name", username);
                            smartmeter.add("profile", profiler);
                            graph.index("nodes", smartmeter, "name", null);

                            if (connections < 30) {
                                concentrator.add("smartmeters", smartmeter);
                                connections++;
                            } else {
                                continue;
                                //backup.add("smartmeters", smartmeter);
                            }
                            while ((line[0] = br.readLine()) != null) {
                                try {

                                    splitted[0] = line[0].split(cvsSplitBy);
                                    if (splitted[0].length != 2) {
                                        continue;
                                    }
                                    timestamp[0] = Long.parseLong(splitted[0][0]);
                                    powerValue[0] = Integer.parseInt(splitted[0][1]);
                                    counts[0]++;
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
                                            result.rel("profile", (profilers) -> {
                                                long s = System.nanoTime();
                                                ((GaussianMixtureNode) profilers[0]).learnVector(new double[]{ElectricMeasure.convertTime(timestamp[0])*24,pv},null);
                                                long t = System.nanoTime();
                                                accumulator[0] += (t - s);
                                                profilers[0].free();
                                            });

                                            result.free();
                                        }
                                    });
                                    globaltotal[0]++;
                                    if (globaltotal[0] % 1000000 == 0) {
                                        long endtime = System.nanoTime();
                                        double restime = (globaltotal[0]) / ((endtime - starttime) / 1000000.0);
                                        System.out.println("Loaded " + globaltotal[0] / 1000000.0 + " m power records in " + restime + " kv/s users " + nuser);
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
                    System.out.println("Counter : " + counts[0]);
                    starttime = System.nanoTime();



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        System.out.println("test done");


    }
    public static long[] shuffle(long[] ids, Random rand){
        for(int i=ids.length-1;i>0;i--){
            int j=rand.nextInt(i+1);
            long temp=ids[i];
            ids[i]=ids[j];
            ids[j]=temp;
        }
        return ids;
    }
}
