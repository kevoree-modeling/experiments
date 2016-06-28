package org.mwg.experiments.kdtree;

import org.mwg.experiments.smartgridprofiling.utility.GaussianProfile;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by assaad on 28/06/16.
 */
public class TestTree {
    public static void main(String[] arg){
        final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
        final double[] err = new double[]{0.5 * 0.5, 0.25 * 0.25, 5 * 5, 10000 * 10000};

        KDTree tree= new KDTree(4);
        try {

            BufferedReader br;
            File dir = new File(csvdir + "NDsim/");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File file : directoryListing) {
                    if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                        continue;
                    }
                    br = new BufferedReader(new FileReader(file));

                    String line;
                    String[] data;

                    int day;
                    double hour;
                    double temperature;
                    double power;

                    while ((line = br.readLine()) != null) {
                        data = line.split(",");
                        //timestamp=Long.parseLong(data[0]);
                        day = Integer.parseInt(data[1]);
                        hour = Double.parseDouble(data[2]);
                        temperature = Double.parseDouble(data[3]);
                        power = Double.parseDouble(data[4]);
                        double[] vector = {day, hour, temperature, power};
                        GaussianProfile profile = (GaussianProfile) tree.nearest(vector);
                        if(profile!=null){
                            if(GaussianMixtureNode.distance(profile.getAvg(),vector,err)<=1.2){
                                double[] prev=profile.getAvg();
                                tree.delete(prev);
                                profile.learn(vector);
                                tree.insert(profile.getAvg(),profile);
                            }
                            else {
                                profile=new GaussianProfile();
                                profile.learn(vector);
                                tree.insert(vector,profile);
                            }
                        }
                        else{
                            profile=new GaussianProfile();
                            profile.learn(vector);
                            tree.insert(vector,profile);
                        }
                    }
                    int x=10;




                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
