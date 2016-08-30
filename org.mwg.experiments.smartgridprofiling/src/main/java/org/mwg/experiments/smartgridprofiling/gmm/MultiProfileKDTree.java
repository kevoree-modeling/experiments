package org.mwg.experiments.smartgridprofiling.gmm;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.LevelDBStorage;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.algorithm.profiling.GaussianTreeNode;
import org.mwg.ml.algorithm.profiling.ProbaDistribution;
import org.mwg.ml.common.matrix.Matrix;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by assaad on 21/06/16.
 */
public class MultiProfileKDTree {
    public static void main(String[] arg) {
      //  final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
        final String csvdir = "./";

        Graph graph = new org.mwg.GraphBuilder()
                .withMemorySize(300000)
//                .saveEvery(10000)
                // .withOffHeapMemory()
                .withStorage(new LevelDBStorage(csvdir + "leveldb/").useNative(false))
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {

            try {
                // 7x24*2x 8 *100
                //Day - hour -temperature - power
                double[] err = new double[]{0.5 * 0.5, 0.25 * 0.25, 0.9 * 0.9, 10 * 10};


                long timecounter = 0;

                BufferedReader br;

                PrintWriter out=new PrintWriter(new FileWriter( csvdir+"statkdtree.csv"));
                out.print("username, LearnTime (ms), profilerNodes, samples, PredictTime (ms), power std, rmse, percent,");
                out.print("min[0], max[0], avg[0], min[1], max[1], avg[1], min[2], max[2], avg[2], min[3], max[3], avg[3], cov.get(00), cov.get(01), cov.get(02), cov.get(03), cov.get(11), cov.get(12), cov.get(13), cov.get(22), cov.get(23), cov.get(33)");
                out.println();

                File dir = new File(csvdir + "NDsim/allusers/");
                //File dir = new File(csvdir + "NDsim/");
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File file : directoryListing) {
                        if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                            continue;
                        }
                        br = new BufferedReader(new FileReader(file));
                        ArrayList<double[]> dataset = new ArrayList<double[]>();
                        String username = file.getName().split("\\.")[0];

                        GaussianTreeNode profiler = (GaussianTreeNode) graph.newTypedNode(0,0,GaussianTreeNode.NAME);

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
                            double[] features = new double[vector.length - 1];
                            System.arraycopy(vector, 0, features, 0, vector.length - 1);

                            start = System.nanoTime();
                            profiler.internalLearn(vector,features, null);
                            timecounter += System.nanoTime() - start;
                        }
                        timecounter = timecounter / 1000000;
                        System.out.println("Time to learn: " +timecounter+" ms, num nodes: "+profiler.getNumNodes()+" learned: "+profiler.getTotal());

                        out.print(username+","+timecounter+","+profiler.getNumNodes()+","+profiler.getTotal()+",");

                        timecounter=timecounter/1000000;

                        final double[] rmse=new double[1];
                        final long[] predicttime= new long[1];


                        predicttime[0]=System.nanoTime();


                        timecounter=0;
                        start=System.nanoTime();
                        for(int i=0;i<dataset.size();i++) {
                            final double[] temp = dataset.get(i);
                            profiler.predictValue(temp, new Callback<Double>() {
                                @Override
                                public void on(Double result) {
                                    rmse[0]+=(temp[3]-result)*(temp[3]-result);
                                }
                            });
                        }

                        timecounter=System.nanoTime()-start;
                        timecounter = timecounter / 1000000;
                        System.out.println("Time to predict: " +timecounter+" ms");
                        out.print(timecounter+",");

                        predicttime[0] =System.nanoTime()-predicttime[0];
                        predicttime[0]=predicttime[0]/1000000;
                        rmse[0]=Math.sqrt(rmse[0]/dataset.size());
                        NumberFormat formatter = new DecimalFormat("#0.00");
                        try {
                            Matrix cov = profiler.getCovariance(profiler.getAvg(), err);
                            if (cov != null) {
                                double srt = Math.sqrt(cov.get(3, 3));
                                double percent = (srt - rmse[0]) * 100 / srt;
                                System.out.println("std: " + formatter.format(srt) + ", rmse: " + formatter.format(rmse[0]) + ", percent: " + formatter.format(percent) + "%");
                                out.print(srt + "," + rmse[0] + "," + percent + ",");
                                double[] min = profiler.getMin();
                                double[] max = profiler.getMax();
                                double[] avg = profiler.getAvg();
                                out.print(min[0] + "," + max[0] + "," + avg[0] + "," + min[1] + "," + max[1] + "," + avg[1] + "," + min[2] + "," + max[2] + "," + avg[2] + "," + min[3] + "," + max[3] + "," + avg[3] + "," + cov.get(0, 0) + "," + cov.get(0, 1) + "," + cov.get(0, 2) + "," + cov.get(0, 3) + "," + cov.get(1, 1) + "," + cov.get(1, 2) + "," + cov.get(1, 3) + "," + cov.get(2, 2) + "," + cov.get(2, 3) + "," + cov.get(3, 3));
                            }
                            System.out.println();
                            out.println();
                            out.flush();
                        }
                        catch (Exception ex){
                            out.println();
                            ex.printStackTrace();
                        }
                    }
                    out.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            graph.disconnect(null);

        });

    }


}
