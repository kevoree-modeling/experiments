package org.mwg.experiments.benchmark;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by assaad on 17/05/16.
 */
public class IOBenchMarck {
    public static void main(String[] arg) {

        final Graph graph = new GraphBuilder()
//                .withOffHeapMemory()
                .withMemorySize(5_000_000)
//                .saveEvery(1_000_000)
                .withStorage(new LevelDBStorage("/Users/assaad/work/github/data/socialgraph/storage/"))
                .withScheduler(new NoopScheduler()).
                        build();

        graph.connect(new Callback<Boolean>() {
                          @Override
                          public void on(Boolean result) {

                              String loc = "/Users/assaad/work/github/data/socialgraph/livejournal.txt";
                              long starttime;
                              long endtime;
                              double res;
                              long total=0;

                              starttime = System.nanoTime();
                              BufferedReader br = null;
                              String line = "";

                              try {
                                  br = new BufferedReader(new FileReader(loc));
                                  while ((line = br.readLine()) != null) {
                                      if (line.startsWith("#")) {
                                          continue;
                                      }

                                      final String[] splits = line.split("\t");

                                      Node[] fromTo= new Node[2];


                                      graph.index(0, 0, "nameidIndex", new Callback<NodeIndex>() {
                                          @Override
                                          public void on(NodeIndex result) {
                                              result.find(splits[0], new Callback<Node[]>() {
                                                  @Override
                                                  public void on(Node[] result) {
                                                      if(result==null|| result.length==0){
                                                          Node res=graph.newNode(0,0);
                                                          //System.out.println(res.id());
                                                          res.set("nameid", Type.STRING, splits[0]);
                                                          graph.index(0, 0, "nameidIndex", new Callback<NodeIndex>() {
                                                              @Override
                                                              public void on(NodeIndex result) {
                                                                  result.addToIndex(res,"nameid");
                                                              }
                                                          });
                                                          fromTo[0] = res;
                                                      }
                                                      else {
                                                          fromTo[0] = result[0];
                                                      }
                                                  }
                                              });
                                          }
                                      });


                                      graph.index(0, 0, "nameidIndex", new Callback<NodeIndex>() {
                                                  @Override
                                                  public void on(NodeIndex result) {
                                                      result.find(splits[1], new Callback<Node[]>() {
                                                          @Override
                                                          public void on(Node[] result) {

                                                              if(result==null|| result.length==0){
                                                                  Node res=graph.newNode(0,0);
                                                                  res.set("nameid", Type.STRING,splits[1]);
                                                                  graph.index(0, 0, "nameidIndex", new Callback<NodeIndex>() {
                                                                      @Override
                                                                      public void on(NodeIndex result) {
                                                                          result.addToIndex(res,"nameid");
                                                                      }
                                                                  });

                                                                  fromTo[1] = res;
                                                              }
                                                              else {
                                                                  fromTo[1] = result[0];
                                                              }

                                                          }
                                                      });
                                                  }
                                              });




                                      fromTo[0].addToRelation("relNode",fromTo[1]);
                                      fromTo[0].free();
                                      fromTo[1].free();

                                      total++;
                                      if(total%10000==0){
                                          endtime=System.nanoTime();
                                          res = ((double) (endtime - starttime)) / (1000000000);
                                          res=total/(res);
                                          System.out.println("total: "+total+" in "+res+" v/s");
                                      }
                                  }

                              } catch (Exception ex) {
                                  System.out.println(ex.getMessage());
                              }
                              endtime=System.nanoTime();
                              res = ((double) (endtime - starttime)) / (1000000000);
                              System.out.println("Loaded :" + total + " edges in " + res + " s, avg: "+(total/(res*1000))+" kv/s");



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
