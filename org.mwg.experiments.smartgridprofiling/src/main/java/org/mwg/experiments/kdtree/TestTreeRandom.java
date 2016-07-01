package org.mwg.experiments.kdtree;

import org.mwg.ml.common.distance.EuclideanDistance;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * Created by assaad on 01/07/16.
 */
public class TestTreeRandom {
    public static void main(String[] arg){



        int dim=3;
        double[] vec=new double[dim];
        Random rand=new Random();
        int num=100000;
        KDTree test=new KDTree(dim);

        for(int i=0;i<num;i++){
            double[] valuecop=new double[vec.length];
            for(int j=0;j<dim;j++){
                vec[j]=rand.nextDouble();
                valuecop[j]=vec[j];
            }
            test.insert(vec,valuecop);
        }


        double[] key=new double[dim];
        for(int i=0;i<dim;i++){
            key[i]=0.1*(i+1);
        }

        NumberFormat formatter = new DecimalFormat("#0.0000");
        System.out.println();

        Object[] result= test.nearest(key,8);

        for(int i=0;i<result.length;i++){
            vec=(double[]) result[i];
            for(int j=0;j<vec.length;j++){
                System.out.print(formatter.format(vec[j])+" ");
            }
            System.out.println("dist: " + formatter.format(new EuclideanDistance().measure(vec,key)));
        }


    }
}
