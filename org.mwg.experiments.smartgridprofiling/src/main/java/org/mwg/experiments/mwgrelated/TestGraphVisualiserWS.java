package org.mwg.experiments.mwgrelated;

import org.mwg.*;
import org.mwg.structure.KDTreeJava;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.distance.GeoDistance;
import org.mwg.structure.tree.KDTree;
import org.mwg.structure.tree.NDTree;
import org.mwg.structure.tree.NDTree2;

import java.util.Random;

/**
 * Created by assaad on 16/09/16.
 */
public class TestGraphVisualiserWS {
    public static void main(String[] arg) {

        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .withMemorySize(10000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NDTree ndTree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);
                KDTree kdtree = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);
                NDTree2 ndTree2 = (NDTree2) graph.newTypedNode(0, 0, NDTree2.NAME);


                Node root = graph.newNode(0, 0);
                root.set("name", Type.STRING, "root");
                root.addToRelation("kdtree", kdtree);
                root.addToRelation("kdtree", ndTree);
                root.addToRelation("kdtree", ndTree2);

                graph.index(0, 0, "KDTREE", new Callback<NodeIndex>() {
                    @Override
                    public void on(NodeIndex result) {
                        result.addToIndex(root, "name");
                    }
                });


                KDTreeJava kdtreejava = new KDTreeJava();
                kdtreejava.setThreshold(KDTree.DISTANCE_THRESHOLD_DEF);

                boolean print = true;


                kdtreejava.setDistance(GeoDistance.instance());
                kdtree.setDistance(Distances.GEODISTANCE);
                ndTree.setDistance(Distances.GEODISTANCE);
                ndTree2.setDistance(Distances.GEODISTANCE);

                int dim = 2;

                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];

                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.25;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }


                ndTree.setBounds(boundMin, boundMax, precisions);
                ndTree2.setBounds(boundMin, boundMax);


                Random random = new Random();
                random.setSeed(125362l);
                int ins = 100;

                graph.save(null);
                long initcache = graph.space().available();


                double[][] keys = new double[ins][];
                Node[] values = new Node[ins];


                for (int i = 0; i < ins; i++) {
                    Node temp = graph.newNode(0, 0);
                    //temp.set("value", Type.DOUBLE, random.nextDouble());

                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }

                    temp.set("key", Type.DOUBLE_ARRAY, key);
                    keys[i] = key;
                    values[i] = temp;
                }

                for (int i = 0; i < ins; i++) {
                    ndTree.insertWith(keys[i], values[i], null);
                    kdtree.insertWith(keys[i], values[i], null);
                    kdtreejava.insert(keys[i], values[i], null);
                    ndTree2.insertWith(keys[i], values[i], null);
                }


                System.out.println("ndtree: " + ndTree.size());
                System.out.println("ndtree2: " + ndTree2.size());

                WSServer ws = new WSServer(graph, 5678);
                ws.start();
            }
        });
    }

}
