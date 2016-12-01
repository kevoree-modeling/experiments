package org.mwg.experiments.mlNodeExperiments;

import org.mwg.*;
import org.mwg.core.scheduler.TrampolineScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;

import java.util.ArrayList;

/**
 * Created by assaad on 25/05/16.
 */
public class TestLookup {
    private static Graph graph; //MWDB graph
    public static GaussianMixtureNode profiler;

    public static void main(String[] arg) {
        graph = new GraphBuilder()
                .withMemorySize(8000000)
//                .saveEvery(10000)
                // .withStorage(new LevelDBStorage("/Users/assaad/work/github/data/consumption/londonpower/leveldb"))
                .withPlugin(new MLPlugin())
                .withScheduler(new TrampolineScheduler())
                .build();

        graph.connect(result -> {
            profiler = (GaussianMixtureNode) graph.newTypedNode(0, 0, GaussianMixtureNode.NAME);

            int max = 1891640;
            ArrayList<Long> ids = new ArrayList<Long>();

            long time = System.nanoTime();
            for (int i = 0; i < max; i++) {
                Node sub = graph.newTypedNode(0, 0, GaussianMixtureNode.NAME);
                sub.set("test", Type.INT, 52);
                ids.add(sub.id());
                sub.free();
            }
            long end = System.nanoTime();
            double res = end - time;
            res = res / 1000000000;
            res = max / res;
            System.out.println("Speed insert: " + res);


            time = System.nanoTime();

            for (int i = 0; i < ids.size(); i++) {
                final long idtemp = ids.get(i);
                graph.lookup(0, 0, idtemp, new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        if (result.id() != idtemp) {
                            System.out.println("Not found " + idtemp);
                        }
                        result.free();
                    }
                });
            }
            end = System.nanoTime();
            res = end - time;
            res = res / 1000000000;
            res = max / res;
            System.out.println("Speed: " + res);


        });
    }
}
