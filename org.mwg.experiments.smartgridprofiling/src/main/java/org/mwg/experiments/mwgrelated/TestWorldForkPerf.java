package org.mwg.experiments.mwgrelated;

import org.mwg.*;
import org.mwg.core.NoopScheduler;
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

        final Graph graph = GraphBuilder.builder()
                .withFactory(new GaussianSlotProfilingNode.Factory())
                .withScheduler(new NoopScheduler())
                .withOffHeapMemory()
                .withMemorySize(10_000_000)
                .withAutoSave(100_000)
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
                try {
                    PrintWriter outTime = new PrintWriter(new File(csvdir + "worldtimePerf.csv"));
                    long newworld = 0;
                    for (int world = 0; world < 100000; world++) {
                        calcTimeStart = System.nanoTime();
                        graph.lookup(newworld, 0, node.id(), new Callback<Node>() {
                            @Override
                            public void on(Node result) {
                                long[] res = (long[]) result.get("rel");
                                result.free();
                            }
                        });
                        calcTime += System.nanoTime() - calcTimeStart;
                        graph.save(null);


                        forkTimeStart = System.nanoTime();
                        newworld = graph.diverge(world);
                        graph.lookup(newworld, 0, node.id(), new Callback<Node>() {
                            @Override
                            public void on(Node result) {
                                Random random = new Random();
                                result.set("rel", new long[]{random.nextLong(), random.nextLong(), random.nextLong()});
                                result.free();
                            }
                        });
                        forkTime += System.nanoTime() - forkTimeStart;

                        outTime.println(world+","+calcTime+","+forkTime);
                        if(world%1000==0){
                            System.out.println(world+","+calcTime+","+forkTime);
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
