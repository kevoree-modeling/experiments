package org.mwg.experiments.smartgridprofiling.gmm;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.mwg.Callback;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class GraphBuilder {

    public static void graphFrom(org.mwg.Graph rootGraph, org.mwg.Node rootNode, String name, String relation, Callback<Graph> cb) {
        final Graph graph = new SingleGraph("Model_"+name);
        graph.setStrict(false);
        createNode(graph, rootNode);
        long world=rootNode.world();
        long time=rootNode.time();

        ArrayList<org.mwg.Node> toDraw = new ArrayList<>();
        toDraw.add(rootNode);

        while (toDraw.size()>0){
            ArrayList<org.mwg.Node> temp = new ArrayList<>();
            for(int i=0;i<toDraw.size();i++){
                org.mwg.Node node = toDraw.get(i);
                long[] children=(long[]) node.get(relation);
                if(children!=null && children.length!=0) {
                    CountDownLatch cdt = new CountDownLatch(children.length);

                    for (int j = 0; j < children.length; j++) {
                        rootGraph.lookup(world, time, children[j], new Callback<org.mwg.Node>() {
                            @Override
                            public void on(org.mwg.Node result) {
                                createNode(graph, result);
                                temp.add(result);
                                cdt.countDown();
                            }
                        });
                    }

                    try{
                        cdt.await();
                        createEdges(graph,node,relation,children);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                if(node!=rootNode){
                    node.free();
                }
            }
            toDraw=temp;
        }


        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.stylesheet", styleSheet);
        cb.on(graph);
    }

    private static void createNode(Graph graph, org.mwg.Node node) {
        Node n = graph.addNode(node.id() + "");
        n.addAttribute("ui.label", node.id() + ":" + node.toString());
    }

    private static void createEdges(Graph graph, org.mwg.Node node, String relation, long[] relatedElems) {
        if (relatedElems != null) {
            for (int i = 0; i < relatedElems.length; i++) {
                Edge e = graph.addEdge(relation+"_"+node.id() + "_" + relatedElems[i], node.id() + "", relatedElems[i] + "");
                if (e != null) {
                    e.addAttribute("ui.label", relation);
                }
            }
        }
    }

    protected static String styleSheet = "graph { padding: 100px; stroke-width: 2px; }"
            + "node { fill-color: orange;  fill-mode: dyn-plain; }"
            + "edge { fill-color: grey; }"
            + "edge .containmentReference { fill-color: blue; }"
            + "node:selected { fill-color: red;  fill-mode: dyn-plain; }"
            + "node:clicked  { fill-color: blue; fill-mode: dyn-plain; }"
            + "node .modelRoot        { fill-color: grey, yellow, purple; fill-mode: dyn-plain; }";
}
