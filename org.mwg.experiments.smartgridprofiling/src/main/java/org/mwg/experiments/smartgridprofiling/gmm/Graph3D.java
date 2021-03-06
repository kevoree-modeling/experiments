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
import org.mwg.ml.algorithm.profiling.ProbaDistribution2;
import org.mwg.ml.common.NDimentionalArray;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution;
import org.mwg.struct.Matrix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
    private ProgressMonitor progressMonitor;

    private org.graphstream.graph.Graph visualGraph; //Graph of MWDB
    private Viewer visualGraphViewer; //Graph Viewer of MWDB
    private View visualGraphView;

    private static Graph graph; //MWDB graph
    public GaussianMixtureNode profiler;
    private int MAXLEVEL;
    private int WIDTH;
    private double FACTOR;
    private int ITER;
    private double THRESHOLD;

    private int selectedCalcLevel = 0;
    private JComboBox<Integer> levelSelector;
    private JLabel graphinfo;
    private JLabel processinginfo;
    private JTextField textX;
    private JTextField textY;
    private JCheckBox fastCalc;
    private double[] err;
    private int[] xConfig = {0, 24, 48};
    private int[] yConfig = {0, 1000, 100};
    private double[] range;
    private boolean automaticMax = false;
    private boolean displayGraph = false;


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
                yConfig[1] = (int) Math.max(profiler.getMax()[1], 1000);
                textY.setText("0," + yConfig[1] + ",100");
                processinginfo.setText("Learning done in " + getTime() + ", generating 3D plot...");
                publish("Learning done in " + getTime() + ", generating 3D plot...");

                graph.save(null);
                System.out.println("Cache size: "+graph.space().available());
                return generatePlot();
            }
            graph.save(null);
            System.out.println("Cache size: "+graph.space().available());
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
                }
            }

            range= new double[2];
            range[1] = (yConfig[1] - yConfig[0]);
            range[1] = range[1] / yConfig[2];

            range[0] = (xConfig[1] - xConfig[0]);
            range[0] = range[0] / xConfig[2];

            for (int i = 0; i < yArray.length; i++) {
                yArray[i] = i * range[1] + yConfig[0];
            }
            for (int i = 0; i < xArray.length; i++) {
                xArray[i] = i * range[0] + xConfig[0];
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
            if (selectedCalcLevel != -1) {
                z = calculateArray(featArray, min, max);
            } else {
                z = calculateArrayDataset(featArray);
            }
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


        private double[] calculateArrayDataset(double[][] features) {
            MultivariateNormalDistribution[] distributions = new MultivariateNormalDistribution[data.size()];
            int global = data.size();

            Matrix covBackup = VolatileMatrix.wrap(null, 2, 2);
            for (int i = 0; i < 2; i++) {
                covBackup.set(i, i, err[i]);
            }

            MultivariateNormalDistribution mvnBackup = new MultivariateNormalDistribution(null, covBackup, false);

            for (int i = 0; i < data.size(); i++) {
                distributions[i] = mvnBackup.clone(data.get(i).getVector());
            }

            ProbaDistribution probabilities = new ProbaDistribution(null, distributions, global);
            return probabilities.calculateArray(features, this);
        }


        //ToDo need optimization
        private double[] calculateArray(double[][] features, double[] min, double[] max) {
            double[][] res = new double[1][];

            CountDownLatch countDownLatch = new CountDownLatch(1);
            profiler.query(selectedCalcLevel, min, max, probabilities -> {
                if (probabilities != null) {

                    if(!fastCalc.isSelected()) {
                        res[0] = probabilities.calculateArray(features, this);
                    }
                    else {
                        ProbaDistribution2 newCalc = new ProbaDistribution2(probabilities.total, probabilities.distributions, probabilities.global);
                        NDimentionalArray temp = newCalc.calculate(min, max, range, err, null);
                        res[0]=new double[features.length];
                        for(int i=0;i<features.length;i++){
                            res[0][i]=temp.get(features[i]);
                        }
                    }
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
        resetProfile();
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

        GridLayout experimentLayout = new GridLayout(9, 1, 0, 0);
        board.setLayout(experimentLayout);

        Integer[] items = new Integer[MAXLEVEL + 2];
        for (int i = 0; i <= MAXLEVEL + 1; i++) {
            items[i] = i - 1;
        }

        levelSelector = new JComboBox<>(items);
        levelSelector.setSelectedItem(levelSelector.getItemAt(1));
        selectedCalcLevel = 0;

        levelSelector.addActionListener(event -> {
            JComboBox comboBox = (JComboBox) event.getSource();
            selectedCalcLevel = (int) comboBox.getSelectedItem();
            feed(0, null); //update the graph
        });

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

        fastCalc = new JCheckBox("fast calculation");
        fastCalc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                feed(0, null);
            }
        });
        board.add(fastCalc);

        getContentPane().add(spUp, BorderLayout.CENTER);




        setSize(1600, 1000);
        setLocationRelativeTo(null);

        if (displayGraph) {
            visualGraph = new SingleGraph("Model");
            visualGraphViewer = new Viewer(visualGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            visualGraphView = visualGraphViewer.addDefaultView(true);   // false indicates "no JFrame".
        }

        clearplot();

        menu();
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
        feed(0, null); //update the proba
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


    private void feed(final int num, double[] temp) {
        if (data != null) {
            if (lock) {
                lock = false;
                progressMonitor = new ProgressMonitor(this, "Loading values...", "", 0, 100);
                operation = new Calculate(num, temp);
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
                resetProfile();
                clearplot();
            } catch (Exception ex) {
                loc = 0;
                data = null;
                JOptionPane.showMessageDialog(null, "Could not load the file ", "Loading failed", JOptionPane.ERROR_MESSAGE);
            }
        }


    }

    private void dataDirInit() {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        fc.setAcceptAllFileFilterUsed(false);
        //
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                    + fc.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    + fc.getSelectedFile());
        } else {
            System.out.println("No Selection ");
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

        menuItem = new JMenuItem("Open a file",
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

     /*   menuItem = new JMenuItem("Open a directory",
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open many files");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataDirInit();
            }
        });
        filemenu.add(menuItem);*/

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



    public void resetProfile() {
        if (profiler != null) {
            profiler.free();
        }
        profiler = (GaussianMixtureNode) graph.newTypedNode(0, 0, GaussianMixtureNode.NAME);

        MAXLEVEL = 3;
        WIDTH=50;
        FACTOR=1.8;
        ITER=20;
        THRESHOLD =1.6;

        profiler.set(GaussianMixtureNode.LEVEL, org.mwg.Type.INT, MAXLEVEL); //max levels allowed
        profiler.set(GaussianMixtureNode.WIDTH, org.mwg.Type.INT, WIDTH); //each level can have 24 components
        profiler.set(GaussianMixtureNode.COMPRESSION_FACTOR, org.mwg.Type.DOUBLE, FACTOR); //Factor of times before compressing, so at 24x10=240, compressions executes
        profiler.set(GaussianMixtureNode.COMPRESSION_ITER, org.mwg.Type.INT, ITER); //iteration in the compression function, keep default
        profiler.set(GaussianMixtureNode.THRESHOLD, org.mwg.Type.DOUBLE, THRESHOLD); //At the lower level, at higher level will be: threashold + level/2 -> number of variance tolerated to insert in the same node
        err = new double[]{0.25 * 0.25, 10 * 10};
        profiler.set(GaussianMixtureNode.RESOLUTION, org.mwg.Type.DOUBLE_ARRAY, err); //Minimum covariance in both axis
    }


    public static void main(String[] args) {
        graph = new GraphBuilder()
                .withMemorySize(300000)
//                .saveEvery(10000)
                // .withOffHeapMemory()
                .withStorage(new LevelDBStorage("./"))
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {
            SwingUtilities.invokeLater(() -> {
                Graph3D ps = new Graph3D();
                ps.setVisible(true);
            });
        });

    }


}
