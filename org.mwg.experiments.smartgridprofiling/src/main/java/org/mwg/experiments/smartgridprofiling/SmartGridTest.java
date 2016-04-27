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
                .withStorage(new LevelDBStorage(csvdir + "leveldb/"))
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

                    File dir = new File(csvdir + "users/");
                    File[] directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        for (File file : directoryListing) {
                            if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                                continue;
                            }
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            username = file.getName().split("\\.")[0];
                            Node smartmeter = graph.newNode(0, 0);
                            final Node profiler = graph.newNode(0, 0, GaussianSlotProfiling.NAME);
                            profiler.set(GaussianSlotProfiling.SLOTSNUMBER, 12); //one slot every hour

                            smartmeter.set("name", username);
                            smartmeter.add("profile", profiler);
                            graph.index("nodes", smartmeter, new String[]{"name"}, null);

                            while ((line = br.readLine()) != null) {
                                try {

                                    splitted = line.split(cvsSplitBy);
                                    if (splitted.length != 2) {
                                        continue;
                                    }
                                    timestamp = Long.parseLong(splitted[0]);
                                    powerValue = Integer.parseInt(splitted[1]);
                                    final int pv = powerValue;

                                    smartmeter.jump(timestamp, new Callback<Node>() {
                                        @Override
                                        public void on(Node result) {


                                            result.set("power", pv);

                                         /*   result.rel("profile", ( profilers) -> {
                                                ((GaussianSlotProfiling) profilers[0]).learn(new double[]{pv});
                                                profilers[0].free();
                                            });*/

                                            result.free();
                                        }
                                    });


                                    globaltotal++;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                            smartmeter.free();

                            nuser++;
                            if (nuser % 10 == 0) {
                                System.out.println(nuser);
                            }
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
