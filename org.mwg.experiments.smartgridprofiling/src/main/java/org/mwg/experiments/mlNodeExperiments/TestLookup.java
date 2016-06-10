package org.mwg.experiments.mlNodeExperiments;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.algorithm.profiling.GaussianGmmNode;

import java.util.ArrayList;

/**
 * Created by assaad on 25/05/16.
 */
public class TestLookup {
    private static Graph graph; //MWDB graph
    public static GaussianGmmNode profiler;

    public static void main(String[] arg) {
        graph = new GraphBuilder()
                .withMemorySize(100000)
                .saveEvery(10000)
                .withStorage(new LevelDBStorage("/Users/assaad/work/github/data/consumption/londonpower/leveldb"))
                .addNodeType(new GaussianGmmNode.Factory())
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {
            profiler = (GaussianGmmNode) graph.newTypedNode(0, 0, "GaussianGmm");

            int max=100000;
            ArrayList<Long> ids = new ArrayList<Long>();
            for (int i = 0; i < max; i++) {
                Node sub = graph.newTypedNode(0, 0, "GaussianGmm");
                sub.set("test",52);
                ids.add(sub.id());
                sub.free();
            }

            for(int i=0;i<ids.size();i++) {
                final long idtemp = ids.get(i);
                graph.lookup(0, 0,idtemp, new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        if(result.id()!=idtemp){
                            System.out.println("Not found "+idtemp);
                        }
                        result.free();
                    }
                });
            }


        });
    }
}
