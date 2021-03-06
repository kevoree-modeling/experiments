package org.mwg.experiments.mwgrelated;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;

import java.util.Random;

/**
 * Created by assaad on 31/05/16.
 */
public class TestCache {
    public static void main(String[] arg) {
        String csvdir = "./";

        final Random random = new Random();
        final Graph graph = new org.mwg.GraphBuilder()
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
//                .withOffHeapMemory()
                .withMemorySize(1_000_000)
//                .saveEvery(10_000)
                .withStorage(new LevelDBStorage(csvdir))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {
                long starttime = System.nanoTime();
                final long[] globaltotal = new long[1];

                for (int j = 0; j < 5000; j++) {
                    Node test = graph.newNode(0, 0);
                    for (int i = 0; i < 100000; i++) {
                        test.travelInTime(i, new Callback<Node>() {
                            @Override
                            public void on(Node result) {
                                result.set("prop", Type.DOUBLE, random.nextDouble());
                                globaltotal[0]++;
                                result.free();
                            }
                        });
                    }
                    test.free();
                    long endtime = System.nanoTime();
                    double restime = (globaltotal[0]) / ((endtime - starttime) / 1000000.0);
                    graph.save(null);
                    System.out.println("Loaded " + globaltotal[0] / 1000000.0 + " m power records in " + restime + " kv/s users, user: " + j + " cache size " + graph.space().available());

                }

            }
        });
    }
}
