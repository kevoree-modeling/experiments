package org.mwg.experiments.Post;

import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * Created by assaad on 26/05/16.
 */
public class PostExperiment {
    public static void main(String[] arg){
        int NUMSWITCH = 4;
        int NUMITER=5;
        double[] input = new double[NUMSWITCH];
        double output;
        int selection =3;
        Random rand=new Random();
        Correlation learning = new Correlation(NUMSWITCH);


        for(int i=0;i<NUMITER;i++){
            for(int j=0;j<NUMSWITCH;j++){
                input[j]=rand.nextInt(100);
            }
            output=0.23*(input[selection]+rand.nextInt(100))+42;
            learning.feed(input,output);
            double[] res=learning.getCorrelation();
            int pos=learning.getMaxArg(res);

            System.out.print("Iteration "+i+": ");
            NumberFormat formatter = new DecimalFormat("#0.000");
            for(int j=0;j<NUMSWITCH;j++){
                if(j!=NUMSWITCH-1) {
                    System.out.print(formatter.format(res[j]) + " , ");
                }
                else{
                    System.out.println(formatter.format(res[j])+" -> Guess: "+pos);
                }
            }
        }

    }
}
