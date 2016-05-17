package org.mwg.experiments.smartgridprofiling.gmm;


import org.graphstream.ui.swingViewer.Viewer;
import org.math.plot.Plot3DPanel;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.NoopScheduler;
import org.mwg.ml.algorithm.profiling.GaussianGmmNode2;
import org.mwg.ml.algorithm.profiling.ProbaDistribution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;


public class Graph3D extends JFrame implements PropertyChangeListener {

    class Calculate extends SwingWorker<Plot3DPanel, String> {
        private final int num;
        private long starttime;
        private long endtime;
        private double[] temp;


        public Calculate(int num,double[] temp) {
            lock = false;
            this.num = Math.min(num, data.size() - loc);
            this.temp=temp;
        }

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Plot3DPanel doInBackground() {
            if(graphViewer !=null){
                graphViewer.close();
            }
            starttime = System.nanoTime();
            if (loc < data.size() && num > 0 || temp != null) {
                publish("Processing " + num + " values started...");

                if (temp != null) {
                    profiler.learnVector(temp, new Callback<Boolean>() {
                        @Override
                        public void on(Boolean result) {

                        }
                    });
                    reloadProfile();
                } else {
                    for (int i = 0; i < num; i++) {
                        this.setProgress(i * 50 / num);
                        if (isCancelled()) {
                            return null;
                        }
                        if (loc < data.size()) {
                            //ToDO feed the profiler here
                            profiler.learnVector(data.get(loc).getVector(), new Callback<Boolean>() {
                                @Override
                                public void on(Boolean result) {

                                }
                            });
                            reloadProfile();
                            loc++;
                        } else {
                            break;
                        }
                    }
                }
                publish("Processing done in " + getTime() + ", generating 3D plot...");


                	/*
         * Plot the distribution estimated by the sample model
		 */

                double xmax = 24;
                double ymax = 1000;
                double[][] zArray;


                // first create a 100x100 grid
                double[] xArray = new double[100];
                double[] yArray = new double[100];
                zArray = new double[yArray.length][xArray.length];

                double zmax = Double.MIN_VALUE;

                if (profiler.getMax() != null) {
                    xmax = 24;
                    ymax = Math.max(profiler.getMax()[1] * 1.1, ymax);
                }

                for (int i = 0; i < 100; i++) {
                    xArray[i] = i * 0.01 * xmax;
                    yArray[i] = i * 0.01 * ymax;
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

                double[] z = calculateArray(featArray);

                if (isCancelled()) {
                    return null;
                }
                zArray = new double[xArray.length][yArray.length];
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

                Plot3DPanel plot = emptyPlot();

                // add grid plot to the PlotPanel
                plot.addGridPlot("Electric consumption probability distribution", xArray, yArray,
                        zArray);

                plot.setFixedBounds(0, 0, xmax);
                plot.setFixedBounds(1, 0, ymax);
                plot.setFixedBounds(2, 0, zmax);
                return plot;

            }
            return null;
        }

        private void reloadProfile() {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            graph.lookup(0, 0, profid, new Callback<GaussianGmmNode2>() {
                @Override
                public void on(GaussianGmmNode2 result) {
                    profiler = result;
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public double[] calculateArray(double[][] features) {
            double[] res = new double[features.length];
            double[] err = new double[]{0.25, 10};

            CountDownLatch countDownLatch = new CountDownLatch(1);
            // reloadProfile();
            profiler.generateDistributions(0, err, new Callback<ProbaDistribution>() {
                @Override
                public void on(ProbaDistribution probabilities) {
                    if (probabilities == null) {
                        countDownLatch.countDown();
                        return;
                    } else {
                        for (int i = 0; i < features.length; i++) {
                            res[i] = probabilities.calculate(features[i]);
                            updateProgress(i * (1.0 / (features.length)));
                            if (isCancelled()) {
                                countDownLatch.countDown();
                                return;
                            }
                        }
                    }
                    countDownLatch.countDown();
                }
            });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reloadProfile();
            return res;
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
        /*
         * Executed in event dispatching thread
         */

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
                    spUp.setLeftComponent(pd);
                    spUp.setDividerLocation(getWidth() - 300);
                    progressMonitor.close();
                    org.mwg.experiments.smartgridprofiling.gmm.GraphBuilder.graphFrom(graph, profiler, "Gaussian", GaussianGmmNode2.INTERNAL_SUBGAUSSIAN_KEY, new Callback<org.graphstream.graph.Graph>() {
                        @Override
                        public void on(org.graphstream.graph.Graph result) {
                            graphViewer = result.display();
                        }
                    });

                }
                //ToDo set the display back here
                //elecStat.setText("Electrical Values loaded: " + ((int) mm.getWeight()));
                // compStat.setText("Number of components: "+mm.totalComponents());
                //topStat.setText("Top level components: " + mm.getTopLevelComp());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void updateProgress(double progress) {
            this.setProgress((int) (progress * 50 + 50));
        }


    }

    private ArrayList<ElectricMeasure> data;
    private JSplitPane spUp;
    private boolean lock = true;

    private   Viewer graphViewer =null;

    private Graph3D() {
        initUI();
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
            int progress = ((Integer) event.getNewValue()).intValue();
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
        data = new ArrayList<ElectricMeasure>();
        // some configuration for plotting
        setTitle("Smart Grid consumption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel board = new JPanel(new FlowLayout(FlowLayout.LEFT));
        board.setPreferredSize(new Dimension(300, 850));
        board.setMaximumSize(new Dimension(350, 1000));
        spUp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JPanel(), board);
        spUp.setContinuousLayout(true);
        spUp.setDividerLocation(this.getWidth() - 300);

      /*  this.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent evt) {
                spUp.setDividerLocation(getWidth()-300);
            }
        });*/

        JLabel elecStat = new JLabel();
        board.add(elecStat);

        //compStat=new JLabel();
        // board.add(compStat);

        JLabel topStat = new JLabel();
        board.add(topStat);

        getContentPane().add(spUp, BorderLayout.CENTER);


        setSize(1400, 850);
        setLocationRelativeTo(null);


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


    private int loc = 0;
    private ProgressMonitor progressMonitor;
    private Calculate operation;

    private void feed(final int num, double[] temp) {
        if (data != null) {

            if (lock) {
                lock = false;
                progressMonitor = new ProgressMonitor(this, "Loading values...", "", 0, 100);
                operation = new Calculate(num,temp);
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
                int feat = 2;

                double[] err = new double[feat];
                err[0] = 0.25;
                err[1] = 30;

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
                feed(1,null);
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
                feed(10,null);
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
                feed(100,null);
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
                feed(1000,null);
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
                feed(data.size(),null);
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
                        feed(1,data);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage());
                    }
                }
            }
        });

        menu.add(menuItem);


        setJMenuBar(menuBar);


    }

    private static long profid;
    private static Graph graph;
    public static GaussianGmmNode2 profiler;

    public static void main(String[] args) {
        graph = GraphBuilder
                .builder()

                .withFactory(new GaussianGmmNode2.Factory())
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                profiler = (GaussianGmmNode2) graph.newNode(0, 0, "GaussianGmm2");
                profiler.configMixture(1, 1000);
                profid = profiler.id();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Graph3D ps = new Graph3D();
                        ps.setVisible(true);
                    }
                });
            }
        });

    }


}
