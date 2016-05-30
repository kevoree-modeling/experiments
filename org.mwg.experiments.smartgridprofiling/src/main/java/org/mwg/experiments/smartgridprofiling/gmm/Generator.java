package org.mwg.experiments.smartgridprofiling.gmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Created by assaa_000 on 28/05/2016.
 */
public class Generator {
    public static void main(String[] arg){
        int num=100000;
        generateEqual(num);
        generateLinear(num);
        generateCircle(num);
        generateLineRnd(num);
        generateConstant(num);
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

    public static double convertTime(long timestamp){
        java.sql.Timestamp tiempoint= new java.sql.Timestamp(timestamp);
        LocalDateTime ldt= tiempoint.toLocalDateTime();
        double res= ((double)ldt.getHour())/24+((double)ldt.getMinute())/(24*60)+((double)ldt.getSecond())/(24*60*60);
        return res;
    }
    public static void generateCircle(int num){
        try {
            PrintWriter out = new PrintWriter(new File("./circledist.csv"));
            long time=System.currentTimeMillis();
            Random rand = new Random();
            for(int i=0;i<num;i++){
                double t=convertTime(time)*24;
                double d=Math.sqrt(24*t-t*t);
                if(rand.nextBoolean()){
                    d=25*(20-d)+200;
                    out.println(time+","+d);
                }
                else {
                    d=25*(20+d)+200;
                    out.println(time+","+d);
                }

                time+=30*60*1000;
            }
            out.flush();
            out.close();
        }
        catch (Exception ex){

        }
    }

    public static void generateLine(int num){
        try {
            PrintWriter out = new PrintWriter(new File("./line.csv"));
            long time=System.currentTimeMillis();
            Random rand = new Random();
            for(int i=0;i<num;i++){
                double t=convertTime(time)*24;
                double d=900*t/24;
                out.println(time+","+d);

                time+=30*60*1000;
            }
            out.flush();
            out.close();
        }
        catch (Exception ex){

        }
    }

    public static void generateLineRnd(int num){
        try {
            PrintWriter out = new PrintWriter(new File("./linernd.csv"));
            long time=System.currentTimeMillis();
            Random rand = new Random();
            for(int i=0;i<num;i++){
                double t=convertTime(time)*24;
                double d=900*t/24+rand.nextInt(20);
                out.println(time+","+d);

                time+=30*60*1000;
            }
            out.flush();
            out.close();
        }
        catch (Exception ex){

        }
    }

    public static void generateConstant(int num){
        try {
            PrintWriter out = new PrintWriter(new File("./constant.csv"));
            long time=System.currentTimeMillis();
            Random rand = new Random();
            for(int i=0;i<num;i++){
                double d=500;
                out.println(time+","+d);
            }
            out.flush();
            out.close();
        }
        catch (Exception ex){

        }
    }
}
