package org.mwg.experiments.post;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by assaad on 02/06/16.
 */
public class TestCorrelation {
    public static void main(String[] arg){
        try {
            BufferedReader br = new BufferedReader(new FileReader("/Users/assaad/work/github/mwDB/mlLog.csv"));
            String line="";
            Correlation c = new Correlation(20);
            while ((line = br.readLine()) != null){
                String[] split=line.split(";");
                int i=Integer.parseInt(split[0]);
                double x=Double.parseDouble(split[1]);
                double y=Double.parseDouble(split[2]);
                c.feedOne(i,x,y);
            }
            double[] corr= c.getCorrelation();
            int x=0;


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
