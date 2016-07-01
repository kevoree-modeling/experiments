package org.mwg.experiments.kdtree;

import org.mwg.experiments.smartgridprofiling.utility.GaussianProfile;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.common.matrix.Matrix;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by assaad on 28/06/16.
 */
public class TestTree {
    public static void main(String[] arg){
        final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

        KDTree tree= new KDTree(3);
        try {

            PrintWriter out=new PrintWriter(new FileWriter( csvdir+"stattree.csv"));
            BufferedReader br;
            File dir = new File(csvdir + "NDsim/");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File file : directoryListing) {
                    if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                        continue;
                    }
                    br = new BufferedReader(new FileReader(file));

                    String line;
                    String[] data;

                    int day;
                    double hour;
                    double temperature;
                    double power;
                    ArrayList<double[]> dataset=new ArrayList<double[]>();


                    while ((line = br.readLine()) != null) {
                        data = line.split(",");
                        //timestamp=Long.parseLong(data[0]);
                        day = Integer.parseInt(data[1]);
                        hour = Double.parseDouble(data[2]);
                        temperature = Double.parseDouble(data[3]);
                        power = Double.parseDouble(data[4]);
                        double[] vector = {day, hour, temperature};
                        double[] learned={day, hour, temperature, power};
                        dataset.add(learned);
                        GaussianProfile profile = (GaussianProfile) tree.nearest(vector);
                        if (profile != null) {
                            double[] prev = profile.getAvg();
                            double[] prevt=new double[3];
                            prevt[0]=prev[0];
                            prevt[1]=prev[1];
                            prevt[2]=prev[2];
                            tree.delete(prevt);
                            profile.learn(learned);

                            double[] av = profile.getAvg();
                            double[] tav = new double[3];
                            tav[0] = av[0];
                            tav[1] = av[1];
                            tav[2] = av[2];

                            tree.insert(tav, profile);
                        } else {
                            profile = new GaussianProfile();
                            profile.learn(learned);

                            double[] av = profile.getAvg();
                            double[] tav = new double[3];
                            tav[0] = av[0];
                            tav[1] = av[1];
                            tav[2] = av[2];

                            tree.insert(tav, profile);
                        }

                    }
                    System.out.println("tree has: "+tree.get_count()+" nodes");

                    double rmse=0;
                    final long[] predicttime= new long[1];


                    predicttime[0]=System.nanoTime();


                    GaussianProfile global = new GaussianProfile();
                    for(int i=0;i<dataset.size();i++) {
                        final double[] temp = dataset.get(i);
                        global.learn(temp);
                        double[] tav = new double[3];
                        tav[0] = temp[0];
                        tav[1] = temp[1];
                        tav[2] = temp[2];

                        GaussianProfile profile = (GaussianProfile) tree.nearest(tav);
                        double pred=profile.getAvg()[3];
                        rmse+=(temp[3]-pred)*(temp[3]-pred);
                    }

                    predicttime[0] =System.nanoTime()-predicttime[0];
                    predicttime[0]=predicttime[0]/1000000;
                    rmse=Math.sqrt(rmse/dataset.size());
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    Matrix cov=global.getCovariance();
                    if(cov!=null) {
                        double srt = Math.sqrt(cov.get(3, 3));
                        double percent = (srt - rmse) * 100 / srt;
                        System.out.println("std: " + formatter.format(srt) + ", rmse: " + formatter.format(rmse) + ", percent: " + formatter.format(percent) + "%");
                        out.println(srt + "," + rmse + "," + percent);
                        out.flush();
                    }
                    br.close();
                }
                out.close();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
