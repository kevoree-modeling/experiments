package org.mwg.experiments.smartgridprofiling.gmm;

import org.mwg.Graph;
import org.mwg.LevelDBStorage;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.experiments.smartgridprofiling.utility.GaussianProfile;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.common.matrix.Matrix;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by assaad on 21/06/16.
 */
public class MultiProfileSlot {
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

                final int every=5;


                PrintWriter out=new PrintWriter(new FileWriter( csvdir+"stat"+every+".csv"));


                long timecounter=0;
                long globaltotal=0;

                BufferedReader br;

                File dir = new File(csvdir + "NDsim/");
                //File dir = new File(csvdir + "NDsim/allusers/");
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File file : directoryListing) {
                        if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                            continue;
                        }
                        br = new BufferedReader(new FileReader(file));
                        ArrayList<double[]> dataset=new ArrayList<double[]>();
                        String username = file.getName().split("\\.")[0];

                        GaussianProfile[][][] profiles= new GaussianProfile[7][48][50/every];
                        for(int i=0;i<7;i++){
                            for(int j=0;j<48;j++){
                                for(int k=0;k<50/every;k++){
                                    profiles[i][j][k]=new GaussianProfile();
                                }
                            }
                        }
                        GaussianProfile global=new GaussianProfile();


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

                            start=System.nanoTime();
                            profiles[day - 1][(int) (hour * 2)][(int) (temperature + 10)/every].learn(new double[]{power});
                            timecounter+=System.nanoTime()-start;

                            global.learn(new double[] {power});
                            globaltotal++;
                        }



                        timecounter=timecounter/1000000;
                        final int[] pos={3};

                        double rmse=0;
                        final long[] predicttime= new long[1];


                        predicttime[0]=System.nanoTime();


                        for(int i=0;i<dataset.size();i++) {
                            final double[] temp = dataset.get(i);
                            double pred= profiles[(int)(temp[0]-1)][(int)(temp[1]*2)][(int)(temp[2]+10)/every].getAvg()[0];
                            rmse+=(temp[3]-pred)*(temp[3]-pred);
                        }

                        predicttime[0] =System.nanoTime()-predicttime[0];
                        predicttime[0]=predicttime[0]/1000000;
                        rmse=Math.sqrt(rmse/dataset.size());
                        NumberFormat formatter = new DecimalFormat("#0.00");
                        Matrix cov=global.getCovariance();
                        if(cov!=null) {
                            double srt = Math.sqrt(cov.get(0, 0));
                            double percent = (srt - rmse) * 100 / srt;
                            System.out.println("std: " + formatter.format(srt) + ", rmse: " + formatter.format(rmse) + ", percent: " + formatter.format(percent) + "%");
                            out.println(srt + "," + rmse + "," + percent);
                            out.flush();
                        }
                        br.close();
                    }
                }
                out.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }



            graph.disconnect(null);

        });

    }


}
