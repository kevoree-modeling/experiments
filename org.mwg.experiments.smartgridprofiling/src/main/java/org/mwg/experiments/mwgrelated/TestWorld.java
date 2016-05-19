package org.mwg.experiments.mwgrelated;

import org.mwg.*;
import org.mwg.core.NoopScheduler;
import org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode;

import java.util.Random;

/**
 * Created by assaad on 19/05/16.
 */
public class TestWorld {
    final static String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
    public static void main(String[] arg){
        final Graph graph = GraphBuilder.builder()
                .withFactory(new GaussianSlotProfilingNode.Factory())
                .withScheduler(new NoopScheduler())
                .withOffHeapMemory()
                .withMemorySize(10_000_000)
                .withAutoSave(100_000)
                .withStorage(new RocksDBStorage(csvdir + "rocksdb/"))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {
                System.out.println("Available space initially: "+graph.space().available());
                Node node=graph.newNode(0,0);
                System.out.println("Available after node creation: "+graph.space().available());
                node.set("rel",new long[]{1,2,3});

                long newworld=0;
                for(int world=0;world<10;world++){
                    newworld=graph.diverge(world);
                    graph.lookup(newworld, 0, node.id(), new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            Random random=new Random();
                            result.set("rel",new long[]{random.nextLong(),random.nextLong(),random.nextLong()});
                            result.free();
                        }
                    });
                    graph.save(null);
                    System.out.println("Available after world "+world+": " +graph.space().available());
                }


            }
        });
    }
}
