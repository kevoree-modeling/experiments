package org.mwg.experiments.mwgrelated;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by assaad on 19/05/16.
 */
public class TestWorldForkPerf {
    final static String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

    public static void main(String[] arg) {

        final Graph graph = new GraphBuilder()
                .addNodeType(new GaussianSlotProfilingNode.Factory())
                .withScheduler(new NoopScheduler())
                .withOffHeapMemory()
                .withMemorySize(10_000_000)
                .saveEvery(1_000)
                .withStorage(new RocksDBStorage(csvdir + "rocksdb2/"))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {
                Node node = graph.newNode(0, 0);
                node.set("rel", new long[]{1, 2, 3});

                long calcTime = 0;
                long forkTime = 0;
                long calcTimeStart;
                long forkTimeStart;

                long calccumul=0;
                long forkcumul=0;
                try {
                    PrintWriter outTime = new PrintWriter(new File(csvdir + "worldtimePerf.csv"));
                    outTime.println("world,calcTime,calcCumul,forkTime,forcCumul");
                    long newworld = 0;
                    final Random random = new Random();
                    long[] ress=new long[3];

                    for (int world = 0; world < 100000; world++) {
                        calcTimeStart = System.nanoTime();
                        graph.lookup(newworld, 0, node.id(), new Callback<Node>() {
                            @Override
                            public void on(Node result) {
                                long[] res = (long[]) result.get("rel");
                                result.free();
                            }
                        });
                        calcTime = System.nanoTime() - calcTimeStart;
                        graph.save(null);




                        ress[0]=random.nextLong();
                        ress[0]=random.nextLong();
                        ress[0]=random.nextLong();


                        forkTimeStart = System.nanoTime();
                        newworld = graph.fork(world);
                        graph.lookup(newworld, 0, node.id(), new Callback<Node>() {
                            @Override
                            public void on(Node result) {
                                result.set("rel", ress);
                                result.free();
                            }
                        });
                        forkTime = System.nanoTime() - forkTimeStart;


                        calccumul+=calcTime;
                        forkcumul+=forkTime;

                        outTime.println(world+","+calcTime+","+calccumul+","+forkTime+","+forkcumul);
                        outTime.flush();
                        if(world%1000==0){
                            System.out.println(world+","+calcTime+","+calccumul+","+forkTime+","+forkcumul);
                        }
                        //System.out.println("Available after world " + world + ": " + graph.space().available());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }
}
