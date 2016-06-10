package org.mwg.experiments.smartgridprofiling.utility;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.algorithm.profiling.GaussianGmmNode;

/**
 * Created by assaad on 02/06/16.
 */
public class TestConnect {
    public static void main(String[] arg) {
        String csvdir = "./";

        final Graph graph = new org.mwg.GraphBuilder()
                .addNodeType(new GaussianGmmNode.Factory())
                .withScheduler(new NoopScheduler())
                .withOffHeapMemory()
                .withMemorySize(10_000_000)
                .saveEvery(10_000)
                .withStorage(new LevelDBStorage(csvdir).useNative(false))
                .build();
        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {
                System.out.println("Connecting...");
                long start = System.nanoTime();

                graph.findAll(0, 0, "profilers", new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        System.out.println(result.length);
                    }
                });
                long end=System.nanoTime();
                double d=end-start;
                d=d/1000000000;
                System.out.println("Took: "+d +" s to load all profiles");


                start = System.nanoTime();
                graph.findAll(0, 0, "nodes", new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        System.out.println(result.length);
                    }
                });
                end=System.nanoTime();
                d=end-start;
                d=d/1000000000;
                System.out.println("Took: "+d +" s to load all smartmeters");
                System.out.println("Connected!");

                WSServer graphServer = new WSServer(graph, 8050);
                graphServer.start();

            }
        });


    }

}
