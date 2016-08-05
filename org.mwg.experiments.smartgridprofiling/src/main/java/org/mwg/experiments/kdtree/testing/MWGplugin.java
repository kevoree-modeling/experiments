package org.mwg.experiments.kdtree.testing;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.plugin.AbstractPlugin;
import org.mwg.plugin.NodeFactory;

/**
 * Created by assaad on 05/08/16.
 */
public class MWGplugin extends AbstractPlugin {

    public MWGplugin() {
        super();
        //PolynomialNode
        declareNodeType(MWGND.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new MWGND(world, time, id, graph);
            }
        });
    }
}