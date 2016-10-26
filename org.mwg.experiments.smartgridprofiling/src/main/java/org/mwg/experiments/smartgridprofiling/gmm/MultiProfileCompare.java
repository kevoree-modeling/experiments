package org.mwg.experiments.smartgridprofiling.gmm;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.LevelDBStorage;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.experiments.smartgridprofiling.utility.GaussianProfile;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.algorithm.profiling.ProbaDistribution;
import org.mwg.struct.Matrix;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by assaad on 21/06/16.
 */
public class MultiProfileCompare {
    public static void main(String[] arg) {
        final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

        Graph graph = new GraphBuilder()
                .withMemorySize(1000000)
//                .saveEvery(10000)
//                .withOffHeapMemory()
                .withStorage(new LevelDBStorage(csvdir + "leveldb/"))
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {

            try {
                int MAXLEVEL = 3;
                int WIDTH = 100;
                double FACTOR = 2;
                int ITER = 10;
                double THRESHOLD = 1.0;
                int every=1;
                // 7x24*2x 8 *100
                //Day - hour -temperature - power
                double[] err = new double[]{0.5 * 0.5, 0.25 * 0.25, 1 * 1, 10 * 10};

                PrintWriter out= new PrintWriter(new FileWriter(csvdir+"diff.csv"));


                long timecounter = 0;
                long globaltotal = 0;

                BufferedReader br;

               // File dir = new File(csvdir + "NDsim/allusers/");
                File dir = new File(csvdir + "NDsim/");
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File file : directoryListing) {
                        if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                            continue;
                        }
                        GaussianProfile[][][] profiles= new GaussianProfile[7][48][50/every];
                        for(int i=0;i<7;i++){
                            for(int j=0;j<48;j++){
                                for(int k=0;k<50/every;k++){
                                    profiles[i][j][k]=new GaussianProfile();
                                }
                            }
                        }
                        GaussianProfile global=new GaussianProfile();



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
                        double power;
                        long start;

                        while ((line = br.readLine()) != null) {
                            data = line.split(",");
                            //timestamp=Long.parseLong(data[0]);
                            day = Integer.parseInt(data[1]);
                            hour = Double.parseDouble(data[2]);
                            temperature = Double.parseDouble(data[3]);
                            power = Double.parseDouble(data[4]);

                            double[] vector = {day, hour, temperature, power};
                            dataset.add(vector);

                            start = System.nanoTime();
                            profiler.learnVector(vector, null);
                            timecounter += System.nanoTime() - start;
                            profiles[day - 1][(int) (hour * 2)][(int) (temperature + 10)/every].learn(new double[]{power});
                            global.learn(new double[] {power});
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
                                            double pred2=result[3];
                                            double pred= profiles[(int)(temp[0]-1)][(int)(temp[1]*2)][(int)(temp[2]+10)/every].getAvg()[0];

                                            double err2=(result[3] - temp[3]) * (result[3] - temp[3]);
                                            double err=(pred - temp[3]) * (pred - temp[3]);
                                            out.println(temp[3]+","+pred+","+pred2+","+err+","+err2);
                                            out.flush();

                                        }
                                    });
                                }
                                predicttime[0] = System.nanoTime() - predicttime[0];
                                predicttime[0] = predicttime[0] / 1000000;
                                rmse[0] = Math.sqrt(rmse[0] / dataset.size());


                            }
                        });

                        out.close();

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
