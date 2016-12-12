package org.mwg.experiments.mwgrelated;

import org.mwg.*;
import org.mwg.structure.NTree;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.tree.KDTree;
import org.mwg.structure.tree.NDTree;
import org.mwg.structure.tree.SparseNDTree;

/**
 * Created by assaad on 08/12/2016.
 */
public class StructurePluginTest {
    public static void main(String[] arg) {
        Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .build();


        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NTree ndTree = (NTree) graph.newTypedNode(0, 0, NDTree.NAME);
                NTree sparseNDTree = (NTree) graph.newTypedNode(0, 0, SparseNDTree.NAME);

                NTree kdTree = (NTree) graph.newTypedNode(0, 0, KDTree.NAME);
                kdTree.setDistance(Distances.EUCLIDEAN);
                kdTree.setDistanceThreshold(0.001);

                ndTree.setDistance(Distances.EUCLIDEAN); //Default distance
                ndTree.setAt(NDTree.BOUND_MIN, Type.DOUBLE_ARRAY, new double[]{0, 0, 0});
                ndTree.setAt(NDTree.BOUND_MAX, Type.DOUBLE_ARRAY, new double[]{1, 1, 1});
                ndTree.setAt(NDTree.RESOLUTION, Type.DOUBLE_ARRAY, new double[]{0.1, 0.1, 0.1});

                sparseNDTree.setDistance(Distances.EUCLIDEAN); //Default distance
                sparseNDTree.setAt(SparseNDTree.BOUND_MIN, Type.DOUBLE_ARRAY, new double[]{0, 0, 0});
                sparseNDTree.setAt(SparseNDTree.BOUND_MAX, Type.DOUBLE_ARRAY, new double[]{1, 1, 1});
                sparseNDTree.setAt(SparseNDTree.MAX_CHILDREN, Type.INT, 10);
            }
        });



    }
}
