package org.mwg.experiments.smartgridprofiling.gmm;


import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.math.plot.Plot3DPanel;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.LevelDBStorage;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.algorithm.profiling.GaussianGmmNode;
import org.mwg.ml.algorithm.profiling.ProbaDistribution;
import org.mwg.ml.algorithm.profiling.ProgressReporter;
import org.mwg.ml.common.matrix.Matrix;
import org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class Graph3D extends JFrame implements PropertyChangeListener {


    // Graph related fields
    private JSplitPane spUp;    //split panel for GUI
    private org.graphstream.graph.Graph visualGraph; //Graph of MWDB
    private Viewer visualGraphViewer; //Graph Viewer of MWDB
    private ProgressMonitor progressMonitor;
    private View visualGraphView;
    private static Graph graph; //MWDB graph
    public static GaussianGmmNode profiler;
    private static int MAXLEVEL;
    private static int selectedCalcLevel = 0;
    private JComboBox<Integer> levelSelector;
    private static double[] err;


    //Data set related fields
    private ArrayList<ElectricMeasure> data; //Data loaded from the csv file
    private int loc = 0; //Location in the data set reading
    private boolean lock = true;
    private Calculate operation;


    private Graph3D() {
        initUI();
    }


    class Calculate extends SwingWorker<Plot3DPanel, String> implements ProgressReporter {
        private final int num;
        private long starttime;
        private long endtime;
        private double[] temp;


        public Calculate(int num, double[] temp) {
            lock = false;
            this.num = Math.min(num, data.size() - loc);
            this.temp = temp;
        }


        @Override
        public Plot3DPanel doInBackground() {
            starttime = System.nanoTime();
            if ((loc < data.size() && num > 0) || temp != null) {
                publish("Processing " + num + " values started...");

                if (temp != null) {
                    profiler.learnVector(temp, result -> {
                    });
                } else {
                    for (int i = 0; i < num; i++) {
                        this.setProgress(i * 50 / num);
                        if (isCancelled()) {
                            return null;
                        }
                        if (loc < data.size()) {
                            profiler.learnVector(data.get(loc).getVector(), new Callback<Boolean>() {
                                @Override
                                public void on(Boolean result) {

                                }
                            });
                            loc++;
                        } else {
                            break;
                        }
                    }
                }
                System.out.println("Learning done in " + getTime() + ", generating 3D plot...");
                publish("Learning done in " + getTime() + ", generating 3D plot...");
                return generatePlot();
            }
            return generatePlot();
        }


        private Plot3DPanel generatePlot() {
             /*
         * Plot the distribution estimated by the sample model
		 */

            double xmax = 24;
            double ymax = 1000;
            double[][] zArray;

            int xm = 48;
            int ym = 100;

            // first create a 100x100 grid
            double[] xArray = new double[xm + 1];
            double[] yArray = new double[ym + 1];

            double zmax = Double.MIN_VALUE;

            if (profiler.getMax() != null) {
                xmax = 24;
                ymax = Math.max(profiler.getMax()[1] * 1.1, ymax);
            }

            for (int i = 0; i < yArray.length; i++) {
                yArray[i] = i * ymax / ym;
            }
            for (int i = 0; i < xArray.length; i++) {
                xArray[i] = i * xmax / xm;
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


            starttime=System.nanoTime();
            double[] z = calculateArray(featArray);
            //double[] z =calculateArrayDataset(featArray);
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
            System.out.println("Calculating probabilities done in " + getTime());
            publish("Calculating probabilities done in " + getTime());

            Plot3DPanel plot = emptyPlot();

            // add grid plot to the PlotPanel
            plot.addGridPlot("Electric consumption probability distribution", xArray, yArray,
                    zArray);

            plot.setFixedBounds(0, 0, xmax);
            plot.setFixedBounds(1, 0, ymax);
            plot.setFixedBounds(2, 0, zmax);
            return plot;
        }


        private double[] calculateArrayDataset(double[][] features) {

            int[] total=new int[data.size()];
            MultivariateNormalDistribution[] distributions=new MultivariateNormalDistribution[data.size()];
            int global=data.size();

            Matrix covBackup = new Matrix(null, 2, 2);
            for (int i = 0; i < 2; i++) {
                covBackup.set(i, i, err[i]);
            }

            MultivariateNormalDistribution mvnBackup=new MultivariateNormalDistribution(null,covBackup);

            for(int i=0;i<data.size();i++){
                total[i]=1;
                distributions[i]=mvnBackup.clone(data.get(i).getVector());
            }

            ProbaDistribution probabilities= new ProbaDistribution(total,distributions,global);


            double[] res = new double[features.length];
            for (int i = 0; i < features.length; i++) {
                res[i] = probabilities.calculate(features[i]);

                double progress = i * (1.0 / (features.length));
                progress = progress * 50 + 50;
                updateProgress((int) progress);
                if (isCancelled()) {
                    return null;
                }
            }
            return res;
        }

        private double[] calculateArray(double[][] features) {
            double[][] res = new double[1][];

            CountDownLatch countDownLatch = new CountDownLatch(1);
            profiler.generateDistributions(selectedCalcLevel, probabilities -> {
                if (probabilities == null) {
                    countDownLatch.countDown();
                    return;
                } else {
                    res[0] = new double[features.length];
                    for (int i = 0; i < features.length; i++) {
                        res[0][i] = probabilities.calculate(features[i]);

                        double progress = i * (1.0 / (features.length));
                        progress = progress * 50 + 50;
                        updateProgress((int) progress);
                        if (isCancelled()) {
                            countDownLatch.countDown();
                            return;
                        }
                    }
                }
                countDownLatch.countDown();
            });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                // e.printStackTrace();
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

                    visualGraphViewer.close();
                    org.mwg.experiments.smartgridprofiling.gmm.GraphBuilder.graphFrom(graph, visualGraph, profiler, selectedCalcLevel, GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY, result -> visualGraphViewer = result.display());


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


    private Plot3DPanel emptyPlot() {
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

        Integer[] items = new Integer[MAXLEVEL + 1];
        for (int i = 0; i <= MAXLEVEL; i++) {
            items[i] = i;
        }

        levelSelector = new JComboBox<>(items);
        levelSelector.setSelectedItem(levelSelector.getItemAt(MAXLEVEL));
        selectedCalcLevel=MAXLEVEL;

        levelSelector.addActionListener(event -> {
            JComboBox comboBox = (JComboBox) event.getSource();
            selectedCalcLevel = (int) comboBox.getSelectedItem();
            feed(0, null); //update the graph
        });

        JLabel comboLabel = new JLabel("Calculation Level (0: most precise):");
        board.add(comboLabel);
        board.add(levelSelector);


        getContentPane().add(spUp, BorderLayout.CENTER);


        setSize(1600, 1000);
        setLocationRelativeTo(null);

        visualGraph = new SingleGraph("Model");
        visualGraphViewer = new Viewer(visualGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        visualGraphView = visualGraphViewer.addDefaultView(true);   // false indicates "no JFrame".
        //this.add(view);

        menu();
        clearplot();

    }

    private void clearplot() {
        Plot3DPanel pp = emptyPlot();
        pp.addGridPlot("Electric consumption probability distribution", new double[]{0, 24}, new double[]{0, 1000}, new double[][]{{0, 0}, {0, 0}});
        pp.setFixedBounds(0, 0, 24);
        pp.setFixedBounds(1, 0, 1000);
        pp.setFixedBounds(2, 0, 1);
        spUp.setLeftComponent(pp);
        spUp.setDividerLocation(getWidth() - 300);
    }


    private void feed(final int num, double[] temp) {
        if (data != null) {

            if (lock) {
                lock = false;
                progressMonitor = new ProgressMonitor(this, "Loading values...", "", 0, 100);
                operation = new Calculate(num, temp);
                operation.addPropertyChangeListener(this);
                operation.execute();
            } else {
                JOptionPane.showMessageDialog(null, "Please wait till the first process is done", "Process not finished yet", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please open a user csv file first", "No user data loaded", JOptionPane.ERROR_MESSAGE);
        }

    }


    private void dataInit() {

        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                loc = 0;
                data = CsvLoader.loadFile(file.getAbsolutePath());
                JOptionPane.showMessageDialog(null, "Loaded " + data.size() + " measures", "Loading successful", JOptionPane.INFORMATION_MESSAGE);
                clearplot();
            } catch (Exception ex) {
                loc = 0;
                data = null;
                JOptionPane.showMessageDialog(null, "Could not load the file ", "Loading failed", JOptionPane.ERROR_MESSAGE);
            }
        }


    }


    private void menu() {

        //Where the GUI is created:
        JMenuBar menuBar;
        JMenu menu, filemenu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        filemenu = new JMenu("File");
        filemenu.setMnemonic(KeyEvent.VK_I);
        filemenu.getAccessibleContext().setAccessibleDescription(
                "File");
        menuBar.add(filemenu);

        menuItem = new JMenuItem("Open",
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open user Csv file");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataInit();
            }
        });

        filemenu.add(menuItem);

        //Build the first menu.
        menu = new JMenu("Electric Load");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "Electric load menu");
        menuBar.add(menu);

        //a group of JMenuItems
        menuItem = new JMenuItem("Load One Value",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load next electric consumption");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(1, null);
            }
        });

        menu.add(menuItem);

        menuItem = new JMenuItem("Load 10 Values",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load 10 electric consumptions values");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(10, null);
            }
        });

        menu.add(menuItem);

        menuItem = new JMenuItem("Load 100 Values",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_3, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load 1000 electric consumptions values");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(100, null);
            }
        });


        menu.add(menuItem);

        menuItem = new JMenuItem("Load 1000 Values",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_4, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load 1000 electric consumptions values");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(1000, null);
            }
        });

        menu.add(menuItem);

        menuItem = new JMenuItem("Load all Values",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_5, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load all electric consumptions values of this client");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(data.size(), null);
            }
        });

        menu.add(menuItem);
        menu.addSeparator();

        menuItem = new JMenuItem("Load Custom Value",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_6, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load Custom Value");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("InputDialog Example #2");
                String s = (String) JOptionPane.showInputDialog(frame, "Enter the features separated by space",
                        "Enter your custom features",
                        JOptionPane.INFORMATION_MESSAGE);
                if ((s != null) && (s.length() > 0)) {
                    try {
                        String[] splits = s.split(" ");
                        double[] data = new double[2];
                        data[0] = Double.parseDouble(splits[0]);
                        data[1] = Double.parseDouble(splits[1]);
                        feed(1, data);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage());
                    }
                }
            }
        });

        menu.add(menuItem);


        setJMenuBar(menuBar);


    }


    public static void main(String[] args) {
        graph = GraphBuilder
                .builder()
                .withMemorySize(300000)
                .withAutoSave(10000)
               // .withOffHeapMemory()
                .withStorage(new LevelDBStorage("/Users/assaad/work/github/data/consumption/londonpower/leveldb"))
                .withFactory(new GaussianGmmNode.Factory())
                .withScheduler(new NoopScheduler())
                .build();

        MAXLEVEL=4;
        graph.connect(result -> {
            profiler = (GaussianGmmNode) graph.newTypedNode(0, 0, "GaussianGmm");
            profiler.set(GaussianGmmNode.LEVEL_KEY, MAXLEVEL); //max levels allowed
            profiler.set(GaussianGmmNode.WIDTH_KEY, 48);
            profiler.set(GaussianGmmNode.COMPRESSION_FACTOR_KEY, 10); //Fuctor of times before compressing
            profiler.set(GaussianGmmNode.COMPRESSION_ITER_KEY,20); //iteration in the compression function
            err=new double[]{0.25 * 0.25, 10 * 10};
            profiler.set(GaussianGmmNode.PRECISION_KEY, err); //Minimum covariance in both axis

            SwingUtilities.invokeLater(() -> {
                Graph3D ps = new Graph3D();
                ps.setVisible(true);
            });
        });

    }


}
