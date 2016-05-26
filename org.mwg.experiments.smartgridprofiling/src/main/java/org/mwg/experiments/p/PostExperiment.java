package org.mwg.experiments.p;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * Created by assaad on 26/05/16.
 */
public class PostExperiment {
    public static void main(String[] arg){
        int NUMSWITCH = 4;
        int NUMITER=7;
        double[] input = new double[NUMSWITCH];
        double output;
        int selection =3;
        Random rand=new Random();
        Correlation learning = new Correlation(NUMSWITCH);


        for(int i=0;i<NUMITER;i++){
            for(int j=0;j<NUMSWITCH;j++){
                input[j]=rand.nextInt(100);
            }
            output=0.23*(input[selection]+rand.nextInt(100))+42+rand.nextInt(60);
            learning.feed(input,output);
            double[] res=learning.getCorrelation();
            int pos=learning.getMaxArg(res);

            System.out.println("Iteration "+i+": ");
            NumberFormat formatter = new DecimalFormat("#0.000");
            String in="";
            String cor="";

            for(int j=0;j<NUMSWITCH;j++){
                if(j!=NUMSWITCH-1){
                    in=in+ formatter.format(input[j])+" , ";
                    cor=cor+ formatter.format(res[j])+" , ";
                }
                else {
                    in=in+ formatter.format(input[j])+" || output: "+output;
                    cor=cor+ formatter.format(res[j])+" -> Guess: "+pos;
                }
            }
            System.out.println(in);
            System.out.println(cor);
            System.out.println();
        }

    }
}
