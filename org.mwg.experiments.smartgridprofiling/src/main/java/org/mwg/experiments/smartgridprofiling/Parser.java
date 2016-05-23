package org.mwg.experiments.smartgridprofiling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * Created by assaad on 19/05/16.
 */
public class Parser {
    public static void main(String[] arg){
        final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvdir + "worldtime.csv"));
            PrintWriter out = new PrintWriter(new File(csvdir + "worldtimeavg.csv"));
            out.println("world,calctime (ms),forktime(ms),calctimeCumul(s),forkCumul(s),min");
            String line;
            String[] splitted;
            long av1=0;
            long av2=0;
            long l1,l2;
            long cumul1=0;
            long cumul2=0;
            int total=0;
            int counter1=0;
            int counter2=0;
            br.readLine();
            while ((line = br.readLine()) != null) {
                splitted = line.split(",");
                l1=Long.parseLong(splitted[1]);
                l2=Long.parseLong(splitted[2]);

                if(l1>5000000){
                    l1=5000000;
                    counter1++;
                }
                if(l2>5000000){
                    l2=5000000;
                    counter2++;
                }


                l1=l1+2000000;
                cumul1+=l1;
                cumul2+=l2;
                av1+=l1;
                av2+=l2;
                total++;
                if(total==100){
                    double a1=av1;
                    double a2=av2;
                    a1=a1/(total*1000000);
                    a2=a2/(total*1000000);
                    double a3= cumul1/1000000000.0;
                    double a4= cumul2/1000000000.0;
                    out.println((Integer.parseInt(splitted[0])+1)+","+a1+","+a2+","+a3+","+a4+","+splitted[5]);
                    out.flush();
                    total=0;
                    av1=0;
                    av2=0;
                }

            }
            out.close();
            System.out.println(counter1);
            System.out.println(counter2);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }


    }
}
