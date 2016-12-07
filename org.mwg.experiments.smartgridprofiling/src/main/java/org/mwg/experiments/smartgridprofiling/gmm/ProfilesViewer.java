package org.mwg.experiments.smartgridprofiling.gmm;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.math.plot.Plot3DPanel;
import org.mwg.*;
import org.mwg.GraphBuilder;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.ProgressReporter;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.algorithm.profiling.ProbaDistribution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


public class ProfilesViewer extends JFrame implements PropertyChangeListener {


    // Graph related fields
    private JSplitPane spUp;    //split panel for GUI
    private ProgressMonitor progressMonitor;

    private org.graphstream.graph.Graph visualGraph; //Graph of MWDB
    private Viewer visualGraphViewer; //Graph Viewer of MWDB
    private View visualGraphView;

    private static Graph graph; //MWDB graph
    public GaussianMixtureNode profiler;
    public static Node[] allprofiles;

    private int selectedCalcLevel = 0;
    private JComboBox<Integer> levelSelector;
    private JComboBox<Node> userSelector;
    private JLabel graphinfo;
    private JLabel processinginfo;
    private JLabel avginfo;
    private JTextField textX;
    private JTextField textY;
    private double[] err;
    private int[] xConfig = {0, 24, 48};
    private int[] yConfig = {0, 1000, 100};
    private boolean automaticMax = true;
    private boolean displayGraph = false;
    private static String workDir = "/Users/assaad/work/github/data/consumption/preloaded/training300/";
    private static HashMap<String, ProbaDistribution> distributionHashMap = new HashMap<>();


    //Data set related fields
    private ArrayList<ElectricMeasure> data; //Data loaded from the csv file
    private int loc = 0; //Location in the data set reading
    private boolean lock = true;
    private Calculate operation;


    private ProfilesViewer() {
        initUI();
    }


    class Calculate extends SwingWorker<Plot3DPanel, String> implements ProgressReporter {
        private long starttime;
        private long endtime;


        public Calculate() {
            lock = false;
        }


        @Override
        public Plot3DPanel doInBackground() {
            starttime = System.nanoTime();
            return generatePlot();
        }


        private Plot3DPanel generatePlot() {
             /*
         * Plot the distribution estimated by the sample model
		 */

            double[][] zArray;


            // first create a 100x100 grid
            double[] xArray = new double[xConfig[2] + 1];
            double[] yArray = new double[yConfig[2] + 1];

            double zmax = Double.MIN_VALUE;

            if (automaticMax) {
                if (profiler.getMax() != null) {
                    yConfig[1] = (int) (profiler.getMax()[1] * 1.1);
                    textY.setText(yConfig[0]+","+yConfig[1]+","+yConfig[2]);
                }
            }

            double yrange;
            yrange = (yConfig[1] - yConfig[0]);
            yrange = yrange / yConfig[2];

            double xrange;
            xrange = (xConfig[1] - xConfig[0]);
            xrange = xrange / xConfig[2];

            for (int i = 0; i < yArray.length; i++) {
                yArray[i] = i * yrange + yConfig[0];
            }
            for (int i = 0; i < xArray.length; i++) {
                xArray[i] = i * xrange + xConfig[0];
            }

            double[][] featArray = new double[(xArray.length * yArray.length)][2];
            int count = 0;
            for (int i = 0; i < xArray.length; i++) {
                for (int j = 0; j < yArray.length; j++) {
                    double[] point = {xArray[i], yArray[j]};
                    featArray[count] = point;
                    count++;
                }
            }

            double[] min = {xConfig[0], yConfig[0]};
            double[] max = {xConfig[1], yConfig[1]};

            starttime = System.nanoTime();
            double[] z;
            z = calculateArray(featArray, min, max);

            if (isCancelled() || z == null) {
                return null;
            }
            zArray = new double[yArray.length][xArray.length];
            count = 0;
            for (int i = 0; i < xArray.length; i++) {
                for (int j = 0; j < yArray.length; j++) {
                    zArray[j][i] = z[count];
                    if (zArray[j][i] > zmax) {
                        zmax = zArray[j][i];
                    }
                    count++;
                }
            }
            if (zmax == 0) {
                zmax = 1;
            }
            processinginfo.setText("Calculating probabilities done in " + getTime());
            publish("Calculating probabilities done in " + getTime());

            Plot3DPanel plot = emptyPlot();

            // add grid plot to the PlotPanel
            plot.addGridPlot("Electric consumption probability distribution", xArray, yArray,
                    zArray);

            plot.setFixedBounds(0, xConfig[0], xConfig[1]);
            plot.setFixedBounds(1, yConfig[0], yConfig[1]);
            plot.setFixedBounds(2, 0, zmax);
            lock = true;
            return plot;
        }


        //ToDo need optimization
        private double[] calculateArray(double[][] features, double[] min, double[] max) {
            double[][] res = new double[1][];

            CountDownLatch countDownLatch = new CountDownLatch(1);
            profiler.query(selectedCalcLevel, min, max, probabilities -> {
                if (probabilities != null) {
                    res[0] = probabilities.calculateArray(features, this);
                    updateProgress(100);
                    countDownLatch.countDown();
                }
            });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                return null;
            }
            return res[0];
        }


        @Override
        protected void process(java.util.List<String> chunks) {
            if (isCancelled()) {
                return;
            }

            for (String s : chunks) {
                progressMonitor.setNote(s);
            }
        }

        private String getTime() {
            endtime = System.nanoTime();
            double v = endtime - starttime;
            String s;
            NumberFormat formatter = new DecimalFormat("#0.00");
            if (v > 1000000000) {
                v = v / 1000000000;
                s = formatter.format(v) + " s";
            } else if (v > 1000000) {
                v = v / 1000000;
                s = formatter.format(v) + " ms";
            } else {
                s = v + " ns";
            }
            return s;
        }

        @Override
        public void done() {
            lock = true;
            try {
                if (!this.isCancelled()) {
                    publish("Processing done in " + getTime());
                    Plot3DPanel pd = get();
                    if (pd == null) {
                        clearplot();
                    } else {
                        spUp.setLeftComponent(pd);
                        spUp.setDividerLocation(getWidth() - 300);
                        progressMonitor.close();
                    }

                    if (displayGraph) {
                        visualGraphViewer.close();
                        org.mwg.experiments.smartgridprofiling.gmm.GraphBuilder.graphFrom(graph, visualGraph, profiler, selectedCalcLevel, GaussianMixtureNode.INTERNAL_SUBGAUSSIAN, result -> visualGraphViewer = result.display());
                    }

                }
                //ToDo set the display back here
                //elecStat.setText("Electrical Values loaded: " + ((int) mm.getWeight()));
                // compStat.setText("Number of components: "+mm.totalComponents());
                //topStat.setText("Top level components: " + mm.getTopLevelComp());
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }


        @Override
        public void updateProgress(int value) {
            this.setProgress(value);
        }

        @Override
        public void updateInformation(String info) {
            graphinfo.setText(info);
        }


    }

    // executes in event dispatch thread
    public void propertyChange(PropertyChangeEvent event) {
        // if the operation is finished or has been canceled by
        // the user, take appropriate action
        if (progressMonitor.isCanceled()) {
            operation.cancel(true);
        } else if (event.getPropertyName().equals("progress")) {
            // get the % complete from the progress event
            // and set it on the progress monitor
            int progress = (Integer) event.getNewValue();
            progressMonitor.setProgress(progress);
        }
    }


    private static Plot3DPanel emptyPlot() {
        Plot3DPanel plot = new Plot3DPanel("SOUTH");
        plot.setAxisLabel(0, "Time");
        plot.setAxisLabel(1, "Electric load");
        plot.setAxisLabel(2, "Probability");
        return plot;
    }


    private void initUI() {
        data = new ArrayList<>();
        // some configuration for plotting
        setTitle("Smart Grid consumption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel board = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //  board.setPreferredSize(new Dimension(300, 1000));
        // board.setMaximumSize(new Dimension(350, 1000));
        spUp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JPanel(), board);
        // spUp.setPreferredSize(new Dimension(1200, 1000));
        spUp.setContinuousLayout(true);
        spUp.setDividerLocation(this.getWidth() - 300);

        GridLayout experimentLayout = new GridLayout(10, 1, 0, 0);
        board.setLayout(experimentLayout);

        int MAXLEVEL = 3;
        Integer[] items = new Integer[MAXLEVEL + 1];
        for (int i = 0; i <= MAXLEVEL; i++) {
            items[i] = i;
        }

        levelSelector = new JComboBox<>(items);

        levelSelector.addActionListener(event -> {
            JComboBox comboBox = (JComboBox) event.getSource();
            selectedCalcLevel = (int) comboBox.getSelectedItem();
            feed(); //update the graph
        });

        userSelector = new JComboBox<>(allprofiles);

        profiler = (GaussianMixtureNode) userSelector.getItemAt(0);
        selectedCalcLevel = 0;

        userSelector.addActionListener(event -> {
            JComboBox comboBox = (JComboBox) event.getSource();
            profiler = (GaussianMixtureNode) comboBox.getSelectedItem();
            avginfo.setText("Avg vector: " + profiler.getAvg()[0] + " , " + profiler.getAvg()[1]);
            feed();

            /*double[][] testing=CsvLoader.loadArray(workDir+"testing300/"+profiler.toString()+".csv");

            ArrayList<SolutionComparator> ss = new ArrayList<SolutionComparator>();

            for(String s: distributionHashMap.keySet()){
                ProbaDistribution p=distributionHashMap.get(s);
                SolutionComparator sc=new SolutionComparator();
                sc.id=s;
                sc.score=p.addUpProbabilities(testing);
                ss.add(sc);
            }
            System.out.println("Rank: "+SolutionComparator.rank(ss,profiler.toString()));*/


        });

        board.add(userSelector);


        JLabel comboLabel = new JLabel("Calculation Level (0: most precise):");
        board.add(comboLabel);
        board.add(levelSelector);

        JLabel temp = new JLabel("X,Y bounds (min,max,numStep)");
        board.add(temp);
        textX = new JTextField(xConfig[0] + "," + xConfig[1] + "," + xConfig[2]);
        textY = new JTextField(yConfig[0] + "," + yConfig[1] + "," + yConfig[2]);
        board.add(textX);
        board.add(textY);
        JButton updateField = new JButton("Update Space");
        updateField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldPressed();
            }
        });
        board.add(updateField);

        graphinfo = new JLabel("");
        board.add(graphinfo);


        processinginfo = new JLabel("");
        board.add(processinginfo);

        avginfo = new JLabel("Avg vector: " + profiler.getAvg()[0] + " , " + profiler.getAvg()[1]);
        board.add(avginfo);


        getContentPane().add(spUp, BorderLayout.CENTER);


        setSize(1600, 1000);
        setLocationRelativeTo(null);

        if (displayGraph) {
            visualGraph = new SingleGraph("Model");
            visualGraphViewer = new Viewer(visualGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            visualGraphView = visualGraphViewer.addDefaultView(true);   // false indicates "no JFrame".
        }

        clearplot();
        feed();

    }

    private void updateFieldPressed() {
        String xs = textX.getText();
        String ys = textY.getText();
        int[] xres = new int[3];
        int[] yres = new int[3];
        try {
            String[] split = xs.split(",");
            xres[0] = Integer.parseInt(split[0]);
            xres[1] = Integer.parseInt(split[1]);
            xres[2] = Integer.parseInt(split[2]);

            split = ys.split(",");
            yres[0] = Integer.parseInt(split[0]);
            yres[1] = Integer.parseInt(split[1]);
            yres[2] = Integer.parseInt(split[2]);

            if (xres[2] <= 0 || yres[2] <= 0) {
                throw new Exception("Third input should not be negative");
            }
            if (xres[1] <= xres[0] || yres[1] <= yres[0]) {
                throw new Exception("max should be > min");
            }
            xConfig = xres;
            yConfig = yres;


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "X and Y selection should 3 integers each: min, max, (numberOfStep>0). " + ex.getMessage(), "Incorrect input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        feed(); //update the proba
    }

    private void clearplot() {
        Plot3DPanel pp = emptyPlot();
        pp.addGridPlot("Electric consumption probability distribution", new double[]{0, 24}, new double[]{0, 1000}, new double[][]{{0, 0}, {0, 0}});
        pp.setFixedBounds(0, 0, 24);
        pp.setFixedBounds(1, 0, 1000);
        pp.setFixedBounds(2, 0, 1);
        spUp.setLeftComponent(pp);
        spUp.setDividerLocation(getWidth() - 300);
        graphinfo.setText("");
        processinginfo.setText("");
    }


    private void feed() {
        if (data != null) {
            if (lock) {
                lock = false;
                progressMonitor = new ProgressMonitor(this, "Loading values...", "", 0, 100);
                operation = new Calculate();
                operation.addPropertyChangeListener(this);
                operation.execute();
                lock = true;
            } else {
                JOptionPane.showMessageDialog(null, "Please wait till the first process is done", "Process not finished yet", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please open a user csv file first", "No user data loaded", JOptionPane.ERROR_MESSAGE);
        }

    }


    public static void main(String[] args) {
        graph = new GraphBuilder()
                .withMemorySize(300000)
//                .saveEvery(10000)
//                .withOffHeapMemory()
                .withStorage(new LevelDBStorage(workDir).useNative(false))
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {

            graph.index(0, 0, "profilers", new Callback<NodeIndex>() {
                @Override
                public void on(NodeIndex result) {
                    result.find(new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            allprofiles = result;
                  /*  long starttime=System.nanoTime();
                    for (int i = 0; i < result.length; i++) {
                        GaussianMixtureNode gmm = (GaussianMixtureNode) result[i];
                        gmm.query(0, null, null, new Callback<ProbaDistribution>() {
                            @Override
                            public void on(ProbaDistribution result) {
                                distributionHashMap.put(gmm.toString(),result);
                            }
                        });
                    }
                    long endtime = System.nanoTime();
                    double d=endtime-starttime;
                    d=d/1000000000;
                    System.out.println("Hashmap: "+distributionHashMap.size()+" loaded in "+d+" sec");*/

//                    System.out.println(result.length);
//                    try {
//                        PrintWriter pw = new PrintWriter(new File("avg.csv"));
//                        for (int i = 0; i < result.length; i++) {
//                            GaussianMixtureNode gmm=(GaussianMixtureNode)result[i];
//                            pw.println(gmm.toString()+","+gmm.getAvg()[0]+","+gmm.getAvg()[1]);
//                        }
//                        pw.flush();
//                        System.out.println("done");
//                    }catch (Exception ex){
//                        ex.printStackTrace();
//                    }
                        }
                    });
                }
            });


            SwingUtilities.invokeLater(() -> {
                ProfilesViewer ps = new ProfilesViewer();
                ps.setVisible(true);

            });

        });

    }


}
