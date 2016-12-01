package org.mwg.experiments.eurusd;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.ml.common.matrix.MatrixOps;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.ml.common.matrix.blassolver.BlasMatrixEngine;
import org.mwg.ml.common.matrix.blassolver.blas.F2JBlas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

public class TestDbPoly {
    public static void main(String[] arg) {

        //String loc = "/Users/duke/Downloads/eurusd-master/";
        String loc = "/Users/assaad/work/github/eurusd/";

       /* Date d=new Date();
        d.setTime(Long.parseLong("991949460000"));*/


        long starttime;
        long endtime;
        double res;
        final TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();
        //final ArrayList<Long> timestamps = new ArrayList<>();
        //final ArrayList<Double> euros = new ArrayList<>();


        starttime = System.nanoTime();
        String csvFile = loc + "Eur USD database/EURUSD_";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            for (int year = 2000; year < 2015; year++) {

                br = new BufferedReader(new FileReader(csvFile + year + ".csv"));
                while ((line = br.readLine()) != null) {
                    // use comma as separator 2000.05.30,17:35
                    String[] values = line.split(cvsSplitBy);
                    Long timestamp = getTimeStamp(values[0]);
                    Double val = Double.parseDouble(values[1]);
                    eurUsd.put(timestamp, val);
                    //timestamps.add(timestamp);
                    //euros.add(val);
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        System.out.println("Db size: " + eurUsd.size());
        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000000);
        System.out.println("Loaded :" + eurUsd.size() + " values in " + res + " s!");
        // System.out.println("Loaded :" + size + " values in " + res + " s!");


        // System.out.println(eurUsd.firstKey());
        //  System.out.println(eurUsd.lastKey());


        final Graph graph = new GraphBuilder()
//                .withOffHeapMemory()
                .withMemorySize(100_000)
//                .saveEvery(10000)
                .withStorage(new LevelDBStorage("data"))
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler()).
                        build();

        graph.connect(new Callback<Boolean>() {
                          @Override
                          public void on(Boolean result) {
                              try {
                                  BlasMatrixEngine bme = (BlasMatrixEngine) MatrixOps.defaultEngine();
                                  bme.setBlas(new F2JBlas());
                              } catch (Exception ignored) {

                              }

                              long starttime, endtime;
                              double d;
                              Iterator<Long> iter = eurUsd.keySet().iterator();


                              final double precision = 0.01;


                              starttime = System.nanoTime();
                              PolynomialNode polyNode = (PolynomialNode) graph.newTypedNode(0, eurUsd.firstKey(), "Polynomial");
                              polyNode.set(PolynomialNode.PRECISION, Type.DOUBLE, precision);
                              iter = eurUsd.keySet().iterator();
                              for (int i = 0; i < eurUsd.size(); i++) {
                                  if (i % 1000000 == 0 /*|| i > 1600000*/) {
                                      System.out.println(i);
                                  }

                                  final long t = iter.next();
                                  polyNode.travelInTime(t, new Callback<PolynomialNode>() {
                                      @Override
                                      public void on(PolynomialNode result) {
                                          result.learn(eurUsd.get(t), new Callback<Boolean>() {
                                              @Override
                                              public void on(Boolean result) {

                                              }
                                          });
                                          result.free();
                                      }
                                  });
                              }
                              endtime = System.nanoTime();
                              d = (endtime - starttime);
                              d = d / 1000000000;
                              d = eurUsd.size() / d;
                              System.out.println("Polynomial insert speed: " + d + " value/sec");


                              polyNode.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
                                  @Override
                                  public void on(long[] result) {
                                      System.out.println("Polynomial number of timepoints: " + result.length);
                                  }
                              });

                              final int[] error = new int[1];
                              iter = eurUsd.keySet().iterator();
                              starttime = System.nanoTime();
                              for (int i = 0; i < eurUsd.size(); i++) {
                                  if (i % 1000000 == 0) {
                                      System.out.println(i);
                                  }
                                  final long t = iter.next();
                                  polyNode.travelInTime(t, new Callback<PolynomialNode>() {
                                      @Override
                                      public void on(PolynomialNode result) {
                                          try {

                                              result.extrapolate(new Callback<Double>() {
                                                  @Override
                                                  public void on(Double d) {

                                                      if (Math.abs(d - eurUsd.get(t)) > precision) {
                                                          error[0]++;
                                                      }
                                                  }
                                              });
                                          } catch (Exception ex) {
                                              ex.printStackTrace();
                                          }
                                          result.free();
                                      }
                                  });
                              }
                              endtime = System.nanoTime();
                              d = (endtime - starttime);
                              d = d / 1000000000;
                              d = eurUsd.size() / d;
                              System.out.println("Polynomial read speed: " + d + " ms");


                              // System.out.println(error[0]);


                              graph.disconnect(new Callback<Boolean>() {
                                  @Override
                                  public void on(Boolean result) {

                                  }
                              });
                          }
                      }

        );

    }

    public static long getTimeStamp(String s) {
        //2014.11.28 16:31
        SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat(
                "yyyy.MM.dd hh:mm");
        Date lFromDate1 = null;
        try {
            lFromDate1 = datetimeFormatter1.parse(s);
            return lFromDate1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
