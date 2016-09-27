package org.mwg.experiments.smartgridprofiling.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by assaad on 22/09/16.
 */
public class DatasetGen {
   public static void main(String[] arg){
       try {
           PrintWriter out = new PrintWriter(new File("./input.csv"));
           PrintWriter outout = new PrintWriter(new File("./output.csv"));
           Random random=new Random();
           double i1,i2;
           for(int i=0;i<10000;i++){
               i1=random.nextDouble();
               i2=0.5*(Math.sin(i1*Math.PI*2)+1);
               out.println(i1);
               outout.println(i2);
           }
           out.close();
           outout.close();


       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
   }
}
