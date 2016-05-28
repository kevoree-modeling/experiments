package org.mwg.experiments.smartgridprofiling.gmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by assaa_000 on 28/05/2016.
 */
public class Generator {
    public static void main(String[] arg){
        generateEqual(100000);
        generateLinear(100000);


    }

    public static void generateEqual(int num){
        try {
            PrintWriter out = new PrintWriter(new File("./equaldist.csv"));
            long time=System.currentTimeMillis();
            Random rand = new Random();
            for(int i=0;i<num;i++){
                out.println(time+","+rand.nextDouble()*1000);
                time+=30*60*1000;
            }
            out.flush();
            out.close();
        }
        catch (Exception ex){

        }
    }

    public static int getLinnearRandomNumber(int maxSize){
        //Get a linearly multiplied random number
        int randomMultiplier = maxSize * (maxSize + 1) / 2;
        Random r=new Random();
        int randomInt = r.nextInt(randomMultiplier);

        //Linearly iterate through the possible values to find the correct one
        int linearRandomNumber = 0;
        for(int i=maxSize; randomInt >= 0; i--){
            randomInt -= i;
            linearRandomNumber++;
        }

        return linearRandomNumber;
    }

    public static void generateLinear(int num){
        try {
            PrintWriter out = new PrintWriter(new File("./lineardist.csv"));
            long time=System.currentTimeMillis();
            Random rand = new Random();
            for(int i=0;i<num;i++){
                out.println(time+","+getLinnearRandomNumber(1000));
                time+=30*60*1000;
            }
            out.flush();
            out.close();
        }
        catch (Exception ex){

        }
    }
}
