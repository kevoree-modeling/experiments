package org.mwg.experiments.smartgridprofiling;

import org.mwg.*;
import org.mwg.core.NoopScheduler;
import org.mwg.profiling.GaussianSlotProfiling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by assaad on 27/04/16.
 */
public class SmartGridTest {
    final static String csvdir = "/Users/duke/Desktop/londonpower/";

    public static void main(String[] arg) {
        final Graph graph = GraphBuilder.builder()
                .withFactory(new GaussianSlotProfiling.Factory())
                .withScheduler(new NoopScheduler())
                // .withOffHeapMemory()
                .withMemorySize(1_000_000)
                .withAutoSave(10_000)
                .withStorage(new RocksDBStorage(csvdir + "rocksdb/"))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {


                int globaltotal = 0;
                long starttime = System.nanoTime();
                try {

                    String line = "";
                    int nuser = 0;
                    String cvsSplitBy = ",";
                    int powerValue;
                    String username;
                    String[] splitted;
                    long timestamp;

                    File dir = new File(csvdir + "training150/");
                    File[] directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        for (File file : directoryListing) {
                            if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                                continue;
                            }
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            GaussianProfile tempProfile=new GaussianProfile();
                            username = file.getName().split("\\.")[0];
                            Node smartmeter = graph.newNode(0, 0);
                            final Node profiler = graph.newNode(0, 0, GaussianSlotProfiling.NAME);
                            profiler.set(GaussianSlotProfiling.SLOTSNUMBER, 12); //one slot every hour

                            smartmeter.set("name", username);
                            smartmeter.add("profile", profiler);
                           // graph.index("nodes", smartmeter, new String[]{"name"}, null);

                            while ((line = br.readLine()) != null) {
                                try {

                                    splitted = line.split(cvsSplitBy);
                                    if (splitted.length != 2) {
                                        continue;
                                    }
                                    timestamp = Long.parseLong(splitted[0]);
                                    powerValue = Integer.parseInt(splitted[1]);
                                    final int pv = powerValue;
                                    tempProfile.learn(new double[]{pv});
                                    smartmeter.jump(timestamp, new Callback<Node>() {
                                        @Override
                                        public void on(Node result) {


                                            result.set("power", pv);


                                            result.rel("profile", ( profilers) -> {
                                                ((GaussianSlotProfiling) profilers[0]).learn(new double[]{pv});
                                                profilers[0].free();
                                            });

                                            result.free();
                                        }
                                    });
                                    globaltotal++;
                                    if(globaltotal%1000000==0){
                                        long endtime = System.nanoTime();
                                        double restime = (globaltotal)/((endtime - starttime)/1000000.0) ;
                                        System.out.println("Loaded " + globaltotal/1000000.0 + " m power records in " + restime + " kv/s users "+nuser);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                            smartmeter.jump(0,result1 -> {
                                result1.rel("profile", ( profilers) -> {
                                    double[] min =((GaussianSlotProfiling) profilers[0]).getMin();
                                    double[] max=((GaussianSlotProfiling) profilers[0]).getMax();
                                    double[] avg=((GaussianSlotProfiling) profilers[0]).getAvg();
                                    double[] sum=((GaussianSlotProfiling) profilers[0]).getSum();
                                    double[] sumq=((GaussianSlotProfiling) profilers[0]).getSumSquare();
                                    int[] tot=((GaussianSlotProfiling) profilers[0]).getTotal();


                                    double[] minc=tempProfile.getMin();
                                    double[] maxc=tempProfile.getMax();
                                    double[] avgc=tempProfile.getAvg();
                                    double[] sumc=tempProfile.getSum();
                                    double[] sumqc=tempProfile.getSumSquares();
                                    int totc=tempProfile.getTotal();


                                    int x=0;

                                });

                            });



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

                    long endtime = System.nanoTime();
                    double restime = (endtime - starttime) / 1000000000;
                    System.out.println("Loaded " + globaltotal + " power records in " + restime + " s !");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });


    }
}
