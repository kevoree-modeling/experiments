package org.mwg.experiments.smartgridprofiling.gmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by assaad on 11/05/16.
 */
public class CsvLoader {
    public static ArrayList<ElectricMeasure> loadFile(String filename) {
        try {
            String line = "";
            String cvsSplitBy = ",";
            ArrayList<ElectricMeasure> ad = new ArrayList<ElectricMeasure> ();
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                String[] val = line.split(cvsSplitBy);
                Long timestamp=Long.parseLong(val[0]);
                Double value= Double.parseDouble(val[1]);
                ad.add(new ElectricMeasure(timestamp,value));
            }
            br.close();
            // System.out.println("Loaded "+filename);
            return ad;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
