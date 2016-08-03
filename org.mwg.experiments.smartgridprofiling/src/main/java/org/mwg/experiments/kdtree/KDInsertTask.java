package org.mwg.experiments.kdtree;

import org.junit.Assert;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.task.*;

import java.util.ArrayList;

import static org.mwg.task.Actions.*;

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
                n1.set("name", "n1");

                graph.save(null);
                long initcache = graph.space().available();

                Node n2 = graph.newNode(0, 0);
                n2.set("name", "n2");

                Node n3 = graph.newNode(0, 0);
                n3.set("name", "n3");

                n1.add("left", n2);
                n1.add("right", n3);

                Node n4 = graph.newNode(0, 0);
                n4.set("name", "n4");
                n2.add("left", n4);


                Node n5 = graph.newNode(0, 0);
                n5.set("name", "n5");
                n3.add("left", n5);

                Node n6 = graph.newNode(0, 0);
                n6.set("name", "n6");
                n3.add("right", n6);


                Node n7 = graph.newNode(0, 0);
                n7.set("name", "n7");
                n5.add("left", n7);

                Node n8 = graph.newNode(0, 0);
                n8.set("name", "n8");
                n5.add("right", n8);

                Node n9 = graph.newNode(0, 0);
                n9.set("name", "n9");
                n6.add("left", n9);


                n1.set("lev", 0);

                n2.set("lev", 1);
                n3.set("lev", 1);

                n4.set("lev", 2);
                n5.set("lev", 2);
                n6.set("lev", 2);

                n7.set("lev", 3);
                n8.set("lev", 3);
                n9.set("lev", 3);

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

                reccursiveDown.then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        Node current = context.resultAsNodes().get(0);

                        System.out.println("visiting: "+current.get("name"));

                        ArrayList<String> chain = (ArrayList<String>) context.variable("nnl").get(0);
                        chain.add((String) current.get("name"));

                        context.declareVariable("near");
                        context.declareVariable("far");
                        context.declareVariable("parent");

                        context.setVariable("parent", context.wrap(current));


                        long[] leftNode = (long[]) current.get("left");
                        if (leftNode != null && leftNode.length != 0) {
                            context.setVariable("far", context.wrap("left"));
                        }
                        else{
                            context.setVariable("far", context.newResult());
                        }

                        long[] rightNode = (long[]) current.get("right");
                        if (rightNode != null && rightNode.length != 0) {
                            context.setVariable("near", context.wrap("right"));
                        }
                        else{
                            context.setVariable("near", context.newResult());
                        }
                        context.continueTask();
                    }
                }).ifThen(new TaskFunctionConditional() {
                    @Override
                    public boolean eval(TaskContext context) {
                        return (context.variable("near").size()>0);
                    }
                },  fromVar("parent").traverse("{{near}}").subTask(reccursiveDown))
                        .ifThen(new TaskFunctionConditional() {
                    @Override
                    public boolean eval(TaskContext context) {
                        return (context.variable("far").size()>0);
                    }
                }, fromVar("parent").traverse("{{far}}").subTask(reccursiveDown));



                final ArrayList<String> nnl=new ArrayList<String>();


                TaskContext tc = reccursiveDown.prepareWith(graph, n1, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        result.free();
                        for(String s: nnl){
                            System.out.print(s+" -> ");
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
