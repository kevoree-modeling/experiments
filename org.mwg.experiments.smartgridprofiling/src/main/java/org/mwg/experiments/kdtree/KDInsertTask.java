package org.mwg.experiments.kdtree;

import org.junit.Assert;
import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.task.*;

import java.util.ArrayList;

import static org.mwg.core.task.Actions.newTask;
import static org.mwg.core.task.Actions.readVar;
import static org.mwg.core.task.Actions.traverse;

/**
 * Created by assaad on 27/07/16.
 */
public class KDInsertTask {
    public static void main(String[] arg) {
        Graph graph = new GraphBuilder().withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node n1 = graph.newNode(0, 0);
                n1.set("name", Type.STRING, "n1");

                graph.save(null);
                long initcache = graph.space().available();

                Node n2 = graph.newNode(0, 0);
                n2.set("name", Type.STRING, "n2");

                Node n3 = graph.newNode(0, 0);
                n3.set("name", Type.STRING, "n3");

                n1.addToRelation("left", n2);
                n1.addToRelation("right", n3);

                Node n4 = graph.newNode(0, 0);
                n4.set("name", Type.STRING, "n4");
                n2.addToRelation("left", n4);


                Node n5 = graph.newNode(0, 0);
                n5.set("name", Type.STRING, "n5");
                n3.addToRelation("left", n5);

                Node n6 = graph.newNode(0, 0);
                n6.set("name", Type.STRING, "n6");
                n3.addToRelation("right", n6);


                Node n7 = graph.newNode(0, 0);
                n7.set("name", Type.STRING, "n7");
                n5.addToRelation("left", n7);

                Node n8 = graph.newNode(0, 0);
                n8.set("name", Type.STRING, "n8");
                n5.addToRelation("right", n8);

                Node n9 = graph.newNode(0, 0);
                n9.set("name", Type.STRING, "n9");
                n6.addToRelation("left", n9);


                n1.set("lev", Type.INT, 0);

                n2.set("lev", Type.INT, 1);
                n3.set("lev", Type.INT, 1);

                n4.set("lev", Type.INT, 2);
                n5.set("lev", Type.INT, 2);
                n6.set("lev", Type.INT, 2);

                n7.set("lev", Type.INT, 3);
                n8.set("lev", Type.INT, 3);
                n9.set("lev", Type.INT, 3);

                n2.free();
                n3.free();
                n4.free();
                n5.free();
                n6.free();
                n7.free();
                n8.free();
                n9.free();
                graph.save(null);
                Assert.assertTrue(graph.space().available() == initcache);


                Task reccursiveDown = newTask();

                reccursiveDown
                        .then(new Action() {
                            @Override
                            public void eval(TaskContext context) {
                                Node current = context.resultAsNodes().get(0);

                                System.out.println("visiting: " + current.get("name"));

                                ArrayList<String> chain = (ArrayList<String>) context.variable("nnl").get(0);
                                chain.add((String) current.get("name"));

                                context.declareVariable("near");
                                context.declareVariable("far");
                                context.declareVariable("parent");

                                context.setVariable("parent", context.wrap(current));


                                long[] leftNode = (long[]) current.get("left");
                                if (leftNode != null && leftNode.length != 0) {
                                    context.setVariable("far", context.wrap("left"));
                                } else {
                                    context.setVariable("far", context.newResult());
                                }

                                long[] rightNode = (long[]) current.get("right");
                                if (rightNode != null && rightNode.length != 0) {
                                    context.setVariable("near", context.wrap("right"));
                                } else {
                                    context.setVariable("near", context.newResult());
                                }
                                context.continueTask();
                            }
                        })
                        .ifThen(new ConditionalFunction() {
                                    @Override
                                    public boolean eval(TaskContext context) {

                                        return (context.variable("near").size() > 0);
                                    }
                                },
                                newTask()
                                        .then(readVar("parent"))
                                        .then(traverse("{{near}}"))
                                        .map(reccursiveDown))

                        .ifThen(new ConditionalFunction() {
                                    @Override
                                    public boolean eval(TaskContext context) {
                                        return (context.variable("far").size() > 0);
                                    }
                                },
                                newTask()
                                        .then(readVar("parent"))
                                        .then(traverse("{{far}}"))
                                        .map(reccursiveDown));


                final ArrayList<String> nnl = new ArrayList<String>();


                TaskContext tc = reccursiveDown.prepare(graph, n1, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        result.free();
                        for (String s : nnl) {
                            System.out.print(s + " -> ");
                        }
                        System.out.println();
                    }
                });

                TaskResult res = tc.newResult();
                res.add(nnl);

                tc.setGlobalVariable("nnl", res);
                tc.setGlobalVariable("lev", tc.wrap(0));

                reccursiveDown.executeUsing(tc);

                graph.save(null);
                Assert.assertTrue(graph.space().available() == initcache);


            }
        });
    }
}
