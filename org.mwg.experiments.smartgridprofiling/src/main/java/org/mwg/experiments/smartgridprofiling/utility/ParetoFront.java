package org.mwg.experiments.smartgridprofiling.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by assaad on 01/06/16.
 */
public class ParetoFront {
    public static void main(String[] arg) {
        try {
            String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
            BufferedReader br = new BufferedReader(new FileReader(csvdir + "FINAL.csv"));
            PrintWriter pw = new PrintWriter(new FileWriter(csvdir + "pareto.csv"));
            ArrayList<double[]> front = new ArrayList<>();
            ArrayList<String> lines = new ArrayList<>();
            String header=br.readLine();
            String line;
            int dom=0;
            int rep=0;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                lines.add(line);
                double[] vals = new double[4];
                vals[0] = Double.parseDouble(split[12]); //lookup minimize
                vals[1] = Double.parseDouble(split[14]); //compression minimize
                vals[2] = Double.parseDouble(split[16]); //error minimize
                vals[3] = i;

                boolean dominated = false;

                boolean replace = false;
                for (double[] xx : front) {
                    dominated = (xx[0] <= vals[0] && xx[1] <= vals[1] && xx[2] <= vals[2]);
                    replace = (xx[0] > vals[0] && xx[1] > vals[1] && xx[2] > vals[2]);
                    if (replace) {
                        rep++;
                        System.arraycopy(vals, 0, xx, 0, 4);
                    }
                    if(dominated){
                        dom++;
                        break;
                    }
                }
                if(!dominated){
                    front.add(vals);
                }
                i++;
            }

            System.out.println("Dominated "+dom+" replace: "+rep);

            pw.println(header);
            HashSet<Integer> exist = new HashSet<>();
            for(double[] xx: front){
                int index= (int)xx[3];
                if(!exist.contains(index)){
                    pw.println(lines.get(index));
                    pw.flush();
                    exist.add(index);
                }
            }
            pw.close();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
