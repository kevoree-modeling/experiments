package org.mwg.experiments.mwgrelated;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.regression.PolynomialNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeMap;

/**
 * Created by assaad on 27/06/16.
 */
public class PolyTemp {

    final static String csvdir = "/Users/assaad/work/github/data/microwave.csv";

    public static void main(String[] arg) {

            final Graph graph = new GraphBuilder()
                    .withPlugin(new MLPlugin())
                    .withScheduler(new NoopScheduler())
                    .withMemorySize(10_000_000)
                    .saveEvery(100_000)
                    .build();
            graph.connect(new Callback<Boolean>() {
                public void on(Boolean result) {
                    try {
                        final double prec=100;
                        BufferedReader br = new BufferedReader(new FileReader(csvdir));
                        String line;
                        String[] data;

                        TreeMap<Long, Double> tree=new TreeMap<Long, Double>();

                        PolynomialNode poly=null;
                        double[] res=new double[1];
                        int duplicate=0;
                        int total=0;
                        int err=0;

                        while ((line = br.readLine()) != null) {
                            line =line.replace('"',' ');
                            data = line.split(",");
                            long timestamp = Long.parseLong(data[0].trim());
                            double value=Double.parseDouble(data[1].trim());
                            total++;
                            if(tree.keySet().contains(timestamp)){
                                duplicate++;
                            }
                            else{
                                tree.put(timestamp,value);
                            }


                            if(poly==null){
                                poly = (PolynomialNode)graph.newTypedNode(0,timestamp,PolynomialNode.NAME);
                                poly.set(PolynomialNode.PRECISION,prec);
                            }
                            poly.jump(timestamp, new Callback<Node>() {
                                @Override
                                public void on(Node result) {
                                    PolynomialNode p = (PolynomialNode)result;
                                    p.learn(value,null);
                                }
                            });

                            poly.jump(timestamp, new Callback<Node>() {
                                @Override
                                public void on(Node result) {
                                    PolynomialNode p = (PolynomialNode)result;
                                    p.extrapolate(new Callback<Double>() {
                                        @Override
                                        public void on(Double result) {
                                            res[0]=result;
                                        }
                                    });
                                    p.free();
                                }
                            });
                            //System.out.println(value+","+res[0]);
                            if(Math.abs(value-res[0])>prec){
                                System.out.println(total+": "+value+","+res[0]);
                                err++;
                            }

                        }
                        System.out.println("Total: "+total+" duplicate: "+duplicate+" tree size: "+tree.keySet().size());
                        System.out.println("Error: "+err);


                        int tol=0;

                        for(long k: tree.keySet()){
                            double v=tree.get(k);
                            double[] ress=new double[1];
                            poly.jump(k, new Callback<Node>() {
                                @Override
                                public void on(Node result) {
                                    PolynomialNode p=(PolynomialNode) result;
                                    p.extrapolate(new Callback<Double>() {
                                        @Override
                                        public void on(Double result) {
                                            ress[0]=result;
                                        }
                                    });
                                    p.free();
                                }
                            });

                            if(Math.abs(v-ress[0])>prec){
                                tol++;
                            }
                        }
                        System.out.println(tol);


                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
    }
}
