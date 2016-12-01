package org.mwg.experiments.kdtree.testing;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.core.task.Actions;
import org.mwg.task.*;

import static org.mwg.core.task.Actions.newTask;
import static org.mwg.core.task.Actions.traverse;


/**
 * Created by assaad on 05/08/16.
 */
public class MWGND extends BaseNode implements DummyNode {
    public static String NAME = "MWGND";
    private static int counter = 0;

    public MWGND(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }


    @Override
    public void setLeft(Object left) {
        Node l = (Node) left;
        this.set("left", Type.RELATION, new long[]{l.id()});
        this.set("leftid", Type.INT, l.get("pid"));
        int lev = (int) get("level");
        l.set("level", Type.INT, lev + 1);
    }

    @Override
    public void setRight(Object right) {
        Node l = (Node) right;
        this.set("right", Type.RELATION, new long[]{l.id()});
        this.set("rightid", Type.INT, l.get("pid"));
        int lev = (int) get("level");
        l.set("level", Type.INT, lev + 1);
    }

    @Override
    public Object createNode() {
        MWGND res = (MWGND) graph().newTypedNode(0, 0, NAME);
        res.set("level", Type.INT, 0);
        res.set("pid", Type.INT, counter);
        counter++;
        return res;
    }

    @Override
    public void print() {
        long l = -1;
        long r = -1;
        Object ll = get("leftid");
        Object rr = get("rightid");
        if (ll != null) {
            l = (int) ll;
        }
        if (rr != null) {
            r = (int) rr;
        }

        System.out.println("node " + this.get("pid") + ", _lev: " + this.get("level") + " left: " + l + ", right: " + r);
    }

    public static Object createFirst(Graph graph) {
        MWGND res = (MWGND) graph.newTypedNode(0, 0, NAME);
        res.set("level", Type.INT, 0);
        res.set("pid", Type.INT, counter);
        counter++;
        return res;
    }

    @Override
    public void traverseRec(Callback<Boolean> callback) {
        TaskContext tc = traverseTask.prepare(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                result.free();
                callback.on(true);
            }
        });

        tc.defineVariable("lev", 0);
        traverseTask.executeUsing(tc);
    }

    public static Task traverseTask = initTask();

    private static Task initTask() {
        Task recctrav = newTask();
        recctrav
                .ifThen(new ConditionalFunction() {
                            @Override
                            public boolean eval(TaskContext context) {
                                return context.result().size() > 0;
                            }
                        },
                        newTask()
                                .then(Actions.print("{{result}} --> {{lev}}"))
                                .thenDo(new ActionFunction() {
                                    @Override
                                    public void eval(TaskContext context) {
                                        int lev = (int) context.variable("lev").get(0);
                                        //context.defineVariable("parent", context.result());
                                        //context.setVariable("parent", context.result());

                                        context.defineVariable("near", "left");
                                        context.defineVariable("far", "right");

                                        //context.defineVariable("next", "left");
                                        context.defineVariableForSubTask("lev", lev + 1);
                                        context.continueTask();
                                    }
                                })
                                // .traverse("{{next}}")
                                .isolate(
                                        newTask()
                                                .then(traverse("{{near}}"))
                                                .isolate(recctrav))
                                //.readVar("parent")
                                .thenDo(new ActionFunction() {
                                    @Override
                                    public void eval(TaskContext context) {
                                        //System.out.println("current:" + context.result() + "-" + context.variable("lev"));
                                        context.defineVariable("next", "right");
                                        context.continueTask();
                                    }
                                })
                                //  .defineVar("next","right")

                                .isolate(newTask()
                                        .then(traverse("{{far}}"))
                                        .isolate(recctrav))


                );


        /*

        recctrav.then(new Action() {
            @Override
            public void eval(TaskContext context) {
                //Load context variables
                Node root = context.resultAsNodes().get(0);
                if (root == null) {
                    context.continueTask();
                    return;
                }
                int lev = (int) context.variable("lev").get(0);

                String warn = "";
                if ((int) root.get("level") != lev) {
                    warn = " -> ERROR FROM HERE!!";
                }

                System.out.println("T1: " + root.get("pid") + " _lev: " + root.get("level") + " received: " + lev + warn);

                long[] lt = (long[]) root.get("left");

                if (lt != null && lt.length != 0) {
                    context.defineVariable("near", "left");
                    context.defineVariableForSubTask("lev", lev + 1);
                } else {
                    context.defineVariable("near", context.newResult());  //stop the loop
                }

                long[] rt = (long[]) root.get("right");
                if (rt != null && rt.length != 0) {
                    context.defineVariable("far", "right");
                    context.defineVariableForSubTask("lev", lev + 1);
                } else {
                    context.defineVariable("far", context.newResult());  //stop the loop
                }

                context.continueTask();
            }
        })

                .ifThen(new ConditionalFunction() {
                    @Override
                    public boolean eval(TaskContext context) {
                        return (context.variable("near").size() > 0 || context.variable("far").size() > 0);
                    }
                }, asVar("parent"))


                .ifThen(new ConditionalFunction() {
                    @Override
                    public boolean eval(TaskContext context) {
                        return context.variable("near").size() > 0;
                    }
                }, traverse("{{near}}")).map(recctrav)


                .readVar("parent").then(new Action() {
            @Override
            public void eval(TaskContext context) {
                Node root = context.resultAsNodes().get(0);
                if (root == null) {
                    context.continueTask();
                    return;
                }
                int lev = (int) context.variable("lev").get(0);

                String warn = "";
                if ((int) root.get("level") != lev) {
                    warn = " -> ERROR FROM HERE!!";
                }

                System.out.println("T2: " + root.get("pid") + " _lev: " + root.get("level") + " received: " + lev + warn);

                long[] rt = (long[]) root.get("right");
                if (rt != null && rt.length != 0) {
                    context.defineVariable("far", "right");
                    context.defineVariableForSubTask("lev", lev + 1);
                } else {
                    context.defineVariable("far", context.newResult());  //stop the loop
                }
                context.continueTask();
            }
        })


                .ifThen(new ConditionalFunction() {
                    @Override
                    public boolean eval(TaskContext context) {
                        return context.variable("far").size() > 0;
                    }
                }, traverse("{{far}}")).map(recctrav)


                .readVar("parent").then(new Action() {
            @Override
            public void eval(TaskContext context) {
                Node root = context.resultAsNodes().get(0);
                if (root == null) {
                    context.continueTask();
                    return;
                }
                int lev = (int) context.variable("lev").get(0);
                System.out.println("T3: " + root.get("pid") + " _lev: " + root.get("level") + " received: " + lev);
                context.continueTask();
            }
        });
*/
        return recctrav;
    }


}
