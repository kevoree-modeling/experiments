package org.mwg.experiments.mwgrelated;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.task.*;
import org.mwg.task.Action;


import static org.mwg.task.Actions.newTask;
import static org.mwg.task.Actions.setTime;

/**
 * Created by assaad on 01/07/16.
 */
public class TraverseTest {
    public static void main(String[] arg) {
        Graph graph = new GraphBuilder()
                .withMemorySize(300000)
                .saveEvery(10000)
                // .withOffHeapMemory()
                //   .withStorage(new LevelDBStorage("./"))
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {

            graph.save(null);
            System.out.println("0: " + graph.space().available());

            String relName="children";

            Node n1 = graph.newNode(0, 13);


            Node n2 = graph.newNode(0, 13);
            Node n3 = graph.newNode(0, 13);
            Node n4 = graph.newNode(0, 13);

            n1.add(relName, n2);
            n1.add(relName, n3);
            n1.add(relName, n4);


            Node n5 = graph.newNode(0, 13);
            Node n6 = graph.newNode(0, 13);
            n2.add(relName, n5);
            n2.add(relName, n6);

            Node n7 = graph.newNode(0, 13);
            Node n8 = graph.newNode(0, 13);
            n3.add(relName, n7);
            n3.add(relName, n8);

            Node n9 = graph.newNode(0, 13);
            Node n10 = graph.newNode(0, 13);
            n4.add(relName, n9);
            n4.add(relName, n10);

            n2.free();
            n3.free();
            n4.free();
            n5.free();
            n6.free();
            n7.free();
            n8.free();
            n9.free();
            n10.free();


            graph.save(null);
            System.out.println("cache 1: " + graph.space().available());

            Task traverse = newTask();

            traverse.asVar("parent").traverse(relName).then(new Action() {
                @Override
                public void eval(TaskContext context) {

                    Node[] children = (Node[]) context.result();
                    for(Node c: children){
                        System.out.println(((Node)c).id());
                    }
                    if(children!=null) {
                        context.setResult(children[0]);
                    }
                    else{
                        context.setResult(null);
                    }
                }
            }).ifThen(new TaskFunctionConditional() {
                @Override
                public boolean eval(TaskContext context) {
                    return (context.result()!=null);
                }
            },traverse);


            Task mainTask = setTime(13).setWorld(0).inject(n1).executeSubTask(traverse);
            mainTask.execute(graph, new Callback<Object>() {
                @Override
                public void on(Object result) {
                    graph.save(null);
                    System.out.println("main size: "+graph.space().available());

                }
            });



            graph.save(null);
            System.out.println("outside: " + graph.space().available());


        });
    }
}
