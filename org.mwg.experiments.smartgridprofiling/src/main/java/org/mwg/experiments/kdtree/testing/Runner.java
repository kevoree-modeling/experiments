package org.mwg.experiments.kdtree.testing;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;

/**
 * Created by assaad on 05/08/16.
 */
public class Runner {
    public static void main(String[] arg){
        final Graph graph = new GraphBuilder()
                .withPlugin(new MWGplugin())
                .withMemorySize(100000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                //DummyNode init= new JavaND();
                DummyNode init= (DummyNode) MWGND.createFirst(graph);

                int size=11;
                DummyNode[] nodes= new DummyNode[size+1];
                nodes[0]=init;

                for (int i=1;i<size+1;i++){
                    nodes[i]=(DummyNode)init.createNode();
                }

                nodes[0].setLeft(nodes[1]);
                nodes[0].setRight(nodes[2]);

                nodes[1].setLeft(nodes[3]);
                nodes[1].setRight(nodes[4]);

                nodes[3].setRight(nodes[6]);

                nodes[6].setLeft(nodes[7]);
                nodes[6].setRight(nodes[8]);

                nodes[4].setRight(nodes[9]);

                nodes[2].setLeft(nodes[5]);
                nodes[2].setRight(nodes[10]);

                nodes[10].setLeft(nodes[11]);


                for(int i=0;i<size+1;i++){
                    nodes[i].print();
                }
                System.out.println();


                init.traverseRec(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        System.out.println("done");
                    }
                });

            }
        });
    }
}
