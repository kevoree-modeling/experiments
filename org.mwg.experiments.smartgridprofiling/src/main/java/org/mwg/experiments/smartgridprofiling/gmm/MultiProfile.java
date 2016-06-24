package org.mwg.experiments.smartgridprofiling.gmm;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.algorithm.profiling.ProbaDistribution;
import org.mwg.ml.algorithm.profiling.ProbaDistribution2;
import org.mwg.ml.common.NDimentionalArray;
import org.mwg.ml.common.matrix.Matrix;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by assaad on 21/06/16.
 */
public class MultiProfile {
    public static void main(String[] arg) {
        final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

        Graph graph = new org.mwg.GraphBuilder()
                .withMemorySize(300000)
                .saveEvery(10000)
                // .withOffHeapMemory()
                .withStorage(new LevelDBStorage(csvdir + "leveldb/"))
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {

            try {
                int MAXLEVEL = 2;
                int WIDTH = 30;
                double FACTOR = 1.8;
                int ITER = 20;
                double THRESHOLD = 1.6;
                // 7x24*2x 8 *100
                //Day - hour -temperature - power
                double[] err = new double[]{0.5 * 0.5, 0.25 * 0.25, 1 * 1, 10 * 10};


                long timecounter = 0;
                long globaltotal = 0;

                BufferedReader br;

                File dir = new File(csvdir + "NDsim/allusers/");
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File file : directoryListing) {
                        if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                            continue;
                        }
                        br = new BufferedReader(new FileReader(file));
                        ArrayList<double[]> dataset = new ArrayList<double[]>();
                        String username = file.getName().split("\\.")[0];
                        GaussianMixtureNode profiler = (GaussianMixtureNode) graph.newTypedNode(0, 0, GaussianMixtureNode.NAME);


                        profiler.set(GaussianMixtureNode.LEVEL, MAXLEVEL); //max levels allowed
                        profiler.set(GaussianMixtureNode.WIDTH, WIDTH); //each level can have 24 components
                        profiler.set(GaussianMixtureNode.COMPRESSION_FACTOR, FACTOR); //Factor of times before compressing, so at 24x10=240, compressions executes
                        profiler.set(GaussianMixtureNode.COMPRESSION_ITER, ITER); //iteration in the compression function, keep default
                        profiler.set(GaussianMixtureNode.THRESHOLD, THRESHOLD); //At the lower level, at higher level will be: threashold + level/2 -> number of variance tolerated to insert in the same node
                        profiler.set(GaussianMixtureNode.PRECISION, err); //Minimum covariance in both axis


                        String line;
                        String[] data;
                        //long timestamp;
                        int day;
                        double hour;
                        double temperature;
                        int power;
                        long start;

                        while ((line = br.readLine()) != null) {
                            data = line.split(",");
                            //timestamp=Long.parseLong(data[0]);
                            day = Integer.parseInt(data[1]);
                            hour = Double.parseDouble(data[2]);
                            temperature = Double.parseDouble(data[3]);
                            power = Integer.parseInt(data[4]);

                            double[] vector = {day, hour, temperature, power};
                            dataset.add(vector);

                            start = System.nanoTime();
                            profiler.learnVector(vector, null);
                            timecounter += System.nanoTime() - start;
                            globaltotal++;
                        }


                        timecounter = timecounter / 1000000;
                        final int[] pos = {3};

                        double[] rmse = new double[1];
                        final long[] predicttime = new long[1];

                        profiler.query(0, null, null, new Callback<ProbaDistribution>() {
                            @Override
                            public void on(ProbaDistribution probabilities) {
                                predicttime[0] = System.nanoTime();

                                for (int i = 0; i < dataset.size(); i++) {
                                    final double[] temp = dataset.get(i);


                                    profiler.predictValue(temp, pos, 0, new Callback<double[]>() {
                                        @Override
                                        public void on(double[] result) {
                                            rmse[0] += (result[3] - temp[3]) * (result[3] - temp[3]);
                                        }
                                    });
                                }
                                predicttime[0] = System.nanoTime() - predicttime[0];
                                predicttime[0] = predicttime[0] / 1000000;
                                rmse[0] = Math.sqrt(rmse[0] / dataset.size());


                            }
                        });


                        printinfo(profiler, globaltotal, timecounter, err, rmse[0], predicttime[0]);

                        profiler.free();
                        br.close();
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            graph.disconnect(null);

        });

    }

    public static void printinfo(GaussianMixtureNode profiler, long globaltotal, long timecounter, final double[] err, final double rmse, final long predicttime) {
        System.out.println("learned " + globaltotal + " values in " + timecounter + " ms.");
        System.out.println();

        double[] avg = profiler.getAvg();
        Matrix cov = profiler.getCovariance(avg, err);

        NumberFormat formatter = new DecimalFormat("#0.00");
        System.out.println("     day - hour - temp - power ");
        System.out.println();


        double[] min = profiler.getMin();
        double[] max = profiler.getMax();

        System.out.print("min: ");
        for (int i = 0; i < avg.length; i++) {
            System.out.print(formatter.format(min[i]) + " \t");
        }
        System.out.println();


        System.out.print("max: ");
        for (int i = 0; i < avg.length; i++) {
            System.out.print(formatter.format(max[i]) + " \t");
        }
        System.out.println();


        System.out.print("avg: ");
        for (int i = 0; i < avg.length; i++) {
            System.out.print(formatter.format(avg[i]) + " \t");
        }
        System.out.println();

        System.out.print("std: ");
        for (int i = 0; i < avg.length; i++) {
            System.out.print(formatter.format(Math.sqrt(cov.get(i, i))) + " \t");
        }
        System.out.println();
        System.out.println();

        System.out.println("Covariance matrix: ");

        for (int i = 0; i < cov.rows(); i++) {
            for (int j = 0; j < cov.columns(); j++) {
                System.out.print(formatter.format(cov.get(i, j)) + " \t");
            }
            System.out.println();
        }
        System.out.println();

        long finalGlobaltotal = globaltotal;
        profiler.query(0, null, null, new Callback<ProbaDistribution>() {
            @Override
            public void on(ProbaDistribution result) {
                System.out.println();
                System.out.println("Number of gaussians: " + result.distributions.length);
                double comp = finalGlobaltotal - result.distributions.length;
                comp = comp * 100 / finalGlobaltotal;
                System.out.println("Compression: " + formatter.format(comp) + "%");
                double srt = Math.sqrt(cov.get(3, 3));
                double percent = (srt - rmse) * 100 / srt;
                System.out.println("Accuracy, rmse: " + formatter.format(rmse) + ", percent: " + formatter.format(percent) + "%");
                System.out.println("predicted " + globaltotal + " values in " + predicttime + " ms");
            }
        });

    }

}
