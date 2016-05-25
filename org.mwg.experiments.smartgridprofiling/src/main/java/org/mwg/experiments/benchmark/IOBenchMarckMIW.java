package org.mwg.experiments.benchmark;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by assaad on 17/05/16.
 */
public class IOBenchMarckMIW {
    public static void main(String[] arg) {

        final Graph graph = GraphBuilder.builder()
                .withOffHeapMemory()
                .withMemorySize(5_000_000)
                .withAutoSave(1_000_000)
                .withStorage(new LevelDBStorage("/Users/assaad/work/github/data/socialgraph/storage/"))
                .withScheduler(new NoopScheduler()).
                        build();


        graph.connect(new Callback<Boolean>() {
                          @Override
                          public void on(Boolean result) {

                              String loc = "/Users/assaad/work/github/data/socialgraph/enron.txt";
                              long starttime;
                              long endtime;
                              double res;
                              long total=0;
                              int maxl=4036531;



                              starttime = System.nanoTime();
                              BufferedReader br = null;
                              String line = "";
                              int max=0;
                              ArrayList<Long>[] relations=new ArrayList[maxl];
                              for(int i=0;i<maxl;i++){
                                  relations[i]=new ArrayList<Long>();
                              }

                              try {
                                  br = new BufferedReader(new FileReader(loc));
                                  while ((line = br.readLine()) != null) {
                                      if (line.startsWith("#")) {
                                          continue;
                                      }

                                      final String[] splits = line.split("\t");
                                      int x= Integer.parseInt(splits[0])+1;
                                      long y=Long.parseLong(splits[1])+1;
                                      if(x>max){
                                          max=x;
                                      }
                                      relations[x].add(y);
                                      total++;
                                      if(total%1000000==0){
                                          endtime=System.nanoTime();
                                          res = ((double) (endtime - starttime)) / (1000000000);
                                          res=total/(res*1000);
                                          System.out.println("total: "+total+" in "+res+" kv/s");
                                      }
                                  }

                              } catch (Exception ex) {
                                  ex.printStackTrace();
                                  System.out.println("Error: "+ex.getMessage()+ " ");
                              }


                              for(int i=1;i<max;i++){
                                  Node node=graph.newNode(0,0);
                                  long[] rela=new long[relations[i].size()];
                                  for(int j=0;j<relations[i].size();j++){
                                      rela[j]=relations[i].get(j);
                                  }
                                  node.set("rel",rela);
                                  node.free();
                              }

                              endtime=System.nanoTime();
                              res = ((double) (endtime - starttime)) / (1000000000);
                              System.out.println("Loaded :" + total + " edges in " + res + " s, avg: "+(total/(res*1000))+" kv/s max: "+max);



                              graph.disconnect(new Callback<Boolean>() {
                                  @Override
                                  public void on(Boolean result) {

                                  }
                              });
                          }
                      }

        );
    }
}
