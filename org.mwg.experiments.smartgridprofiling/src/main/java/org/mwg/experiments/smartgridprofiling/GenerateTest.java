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
public class GenerateTest {
    final static String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

    public static void main(String[] arg) {
        final Graph graph = GraphBuilder.builder()
                .withFactory(new GaussianSlotProfiling.Factory())
                .withScheduler(new NoopScheduler())
                // .withOffHeapMemory()
                .withMemorySize(1_000_000)
                .withAutoSave(10_000)
                .withStorage(new LevelDBStorage(csvdir + "leveldb/"))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {

                final long trainingStart = 1348961400000l;
                final long trainingend = 1382000600000l;
                final long testingend = 1390001400000l;


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


                    File dir = new File(csvdir + "users/");
                    File[] directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        for (File file : directoryListing) {
                            if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                                continue;
                            }
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            TreeMap<Long, Integer> trainingSet = new TreeMap<Long, Integer>();
                            TreeMap<Long, Integer> testingSet = new TreeMap<Long, Integer>();
                            username = file.getName().split("\\.")[0];


                            while ((line = br.readLine()) != null) {
                                try {

                                    splitted = line.split(cvsSplitBy);
                                    if (splitted.length != 2) {
                                        continue;
                                    }
                                    timestamp = Long.parseLong(splitted[0]);
                                    powerValue = Integer.parseInt(splitted[1]);

                                    if (timestamp >= trainingStart && timestamp <= trainingend) {
                                        trainingSet.put(timestamp, powerValue);
                                    }
                                    if (timestamp > trainingend && timestamp <= testingend) {
                                        testingSet.put(timestamp, powerValue);
                                    }


                                    globaltotal++;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                            PrintWriter outTraining = new PrintWriter(new File(csvdir + "training/" + username + ".csv"));
                            PrintWriter outTesting = new PrintWriter(new File(csvdir + "testing/" + username + ".csv"));

                            if(trainingSet.keySet().size()!=0) {
                                for (long tt : trainingSet.keySet()) {
                                    outTraining.println(tt + "," + trainingSet.get(tt));
                                }
                                outTraining.close();
                            }

                            if(testingSet.keySet().size()!=0) {
                                for (long tt : testingSet.keySet()) {
                                    outTesting.println(tt + "," + testingSet.get(tt));
                                }
                                outTesting.close();
                            }

                            nuser++;
                            if (nuser % 100 == 0) {
                                System.out.println(nuser);
                            }
                            br.close();
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
