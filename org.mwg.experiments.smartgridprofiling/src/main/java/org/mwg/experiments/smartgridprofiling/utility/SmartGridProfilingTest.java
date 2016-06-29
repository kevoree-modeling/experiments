package org.mwg.experiments.smartgridprofiling.utility;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by assaad on 27/04/16.
 */
public class SmartGridProfilingTest {
    final static int SLOTS=24*2*7;
    final static long PERIOD=7*24*3600*1000;
    final static String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
    //final static String csvdir = "/Users/duke/Desktop/londonpower/";

    public static void main(String[] arg) {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                // .withOffHeapMemory()
                .withMemorySize(1_000_000)
                .saveEvery(10_000)
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
                            final Node profiler = graph.newTypedNode(0, 0, GaussianSlotNode.NAME);
                            profiler.set(GaussianSlotNode.SLOTS_NUMBER, SLOTS); //one slot every hour
                            profiler.set(GaussianSlotNode.PERIOD_SIZE,PERIOD);
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
                                                ((GaussianSlotNode) profilers[0]).learnArray(new double[]{pv});
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
                    globaltotal[0] = 0;
                    //Loading the testing set
                    dir = new File(csvdir + "testing300/");
                    directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        for (File file : directoryListing) {
                            if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                                continue;
                            }
                            username = file.getName().split("\\.")[0];
                            //fetch the node by username
                            graph.find(0, 0, "nodes", "name=" + username, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    Node smartmeter = result[0];
                                    try {
                                        BufferedReader br = new BufferedReader(new FileReader(file));
                                        String line;
                                        while ((line = br.readLine()) != null) {

                                            String[] splitted = line.split(cvsSplitBy);
                                            if (splitted.length != 2) {
                                                continue;
                                            }
                                            long timestamp = Long.parseLong(splitted[0]);
                                            Integer powerValue = Integer.parseInt(splitted[1]);
                                            if (timestamp > maxTesting[0]) {
                                                maxTesting[0] = timestamp;
                                            }
                                            final int pv = powerValue;
                                            smartmeter.jump(timestamp, new Callback<Node>() {
                                                @Override
                                                public void on(Node result) {
                                                    result.set("power", pv);
                                                    result.free();
                                                }
                                            });
                                            globaltotal[0]++;
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                }
                            });
                        }
                    }

                    endtime[0] = System.nanoTime();
                    restime[0] = (endtime[0] - starttime) / 1000000000;
                    System.out.println("Loading test " + globaltotal[0] + " power records in " + restime[0] + " s !");
                    System.out.println("Start training " + minTraining);
                    System.out.println("End training " + maxTraining);
                    System.out.println("End testing " + maxTesting[0]);

                    final Node concentratorProfiler = graph.newTypedNode(0, 0, GaussianSlotNode.NAME);
                    concentratorProfiler.set(GaussianSlotNode.SLOTS_NUMBER, SLOTS); //one slot every hour
                    concentratorProfiler.set(GaussianSlotNode.PERIOD_SIZE,PERIOD);
                    concentrator.add("profile", concentratorProfiler);

                    //Change the connections N hour
                    final Random rand = new Random(minTraining);
                    System.out.println("Initial random: " + rand.nextInt(150));

                /*    long connectionChange = 1 * 3600 * 1000;

                    int counter2=0;
                    for (long time = minTraining; time < maxTesting[0]; time += connectionChange) {

                        counter2++;
                        long finalTime = time;
                        concentrator.jump(time, result1 -> {
                            backup.jump(finalTime, result2 -> {

                                long[] id1 = (long[]) result1.get("smartmeters");
                                long[] id2 = (long[]) result2.get("smartmeters");

                                //mutate arrays here
                                int x = rand.nextInt(100)+50;
                                long[] ida=new long[x];
                                long[] idb=new long[id1.length+id2.length-x];

                                if (x < id1.length) {
                                    id1 = shuffle(id1, rand);

                                    System.arraycopy(id2, 0, idb, 0, id2.length);
                                    System.arraycopy(id1, 0, ida, 0, x);
                                    System.arraycopy(id1, x, idb, id2.length, id1.length - x);

                                    result1.set("smartmeters",ida);
                                    result2.set("smartmeters",idb);
                                } else if (x > id1.length) {
                                    id2 = shuffle(id2, rand);

                                    System.arraycopy(id1, 0, ida, 0, id1.length);
                                    System.arraycopy(id2, 0, idb, 0, idb.length);
                                    System.arraycopy(id2, idb.length, ida, id1.length, ida.length - id1.length);

                                    result1.set("smartmeters",ida);
                                    result2.set("smartmeters",idb);
                                }
                                result1.free();
                                result2.free();

                            });
                        });
                    }
                    System.out.println("Connection changed "+counter2); */

                    long finalMinTraining = minTraining;
                    concentrator.timepoints( Constants.BEGINNING_OF_TIME,Constants.END_OF_TIME, result1 -> {
                        System.out.println(result1.length);
                       concentrator.timepoints(finalMinTraining,maxTesting[0], result2 -> {
                            System.out.println(result2.length);
                        });
                    });

                    long[] xerr=new long[1];
                    //Train global profile
                    long halfHour=1800 *1000;
                    for (long time = minTraining; time < maxTraining; time += halfHour) {
                        long finalTime = time;
                        concentrator.jump(time, result1 -> {
                            double[] val = new double[1];
                            result1.rel("smartmeters", new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    if(result==null){
                                        System.out.println("Connections from concentrator to smart meters null");
                                    }
                                    else {
                                        for (int i = 0; i < result.length; i++) {
                                            Integer value= (Integer)result[i].get("power");
                                            if(value==null){
                                                xerr[0]++;
                                                //System.out.println("Meter "+result[i].get("name")+" has null value at time "+finalTime);
                                                value=0;
                                            }
                                            val[0] += value;
                                            result[i].free();
                                        }
                                    }
                                }
                            });
                          // System.out.println(val[0]);
                            result1.rel("profile", (profilers) -> {
                                ((GaussianSlotNode) profilers[0]).learnArray(val);

                                profilers[0].free();
                            });

                        });
                    }

                    final double[] avg=new double[SLOTS+1];

                    concentrator.jump(maxTraining,result1 -> {
                        result1.rel("profile",result2 -> {
                            double[] temp=((GaussianSlotNode) result2[0]).getAvg();
                            for(int i=0;i<SLOTS+1;i++){
                                avg[i]=temp[i];
                            }
                            result2[0].free();
                        });
                        result1.free();
                    });



                    PrintWriter out = new PrintWriter(new File(csvdir + "result.csv"));

                    GaussianProfile gp = new GaussianProfile();

                    final int[] count=new int[1];
                    for (long time = maxTraining; time < maxTesting[0]; time += halfHour) {
                        long finalTime = time;
                        double[] predictions=new double[3]; //pred[0]: real value, pred[1]= sum of fine grained
                        predictions[2]=avg[GaussianSlotNode.getIntTime(time,SLOTS,GaussianSlotNode.PERIOD_SIZE_DEF)]; //pred[2]= global
                        concentrator.jump(finalTime,result1 -> {
                            result1.rel("smartmeters", new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    if(result==null){
                                        System.out.println("Connections from concentrator to smart meters null");
                                    }
                                    else {
                                        for (int i = 0; i < result.length; i++) {
                                            Integer value= (Integer)result[i].get("power");
                                            if(value==null){
                                                //System.out.println("Meter "+result[i].get("name")+" has null value at time "+finalTime);
                                                value=0;
                                            }
                                            predictions[0] += value;
                                            result[i].rel("profile", new Callback<Node[]>() {
                                                @Override
                                                public void on(Node[] result) {
                                                    ((GaussianSlotNode) result[0]).predict(new Callback<double[]>() {
                                                        @Override
                                                        public void on(double[] result) {
                                                            predictions[1]+=result[0];
                                                        }
                                                    });
                                                    result[0].free();
                                                }
                                            });
                                            result[i].free();
                                        }
                                    }
                                }
                            });
                            result1.free();


                            //Compare the 3 values here :)
                            double[] errors=new double[2];
                            errors[0]=Math.abs(predictions[1]-predictions[0]);
                            errors[1]=Math.abs(predictions[2]-predictions[0]);
                            out.println(count[0]+" , "+finalTime+" , "+predictions[0]+" , "+predictions[1]+" , "+predictions[2]+" , "+errors[0]+" , "+errors[1]);
                            gp.learn(errors);
                            count[0]++;
                        });
                    }
                    out.close();

                   gp.print();




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
