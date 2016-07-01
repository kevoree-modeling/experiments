package org.mwg.experiments.mwgrelated;

import org.mwg.ml.common.matrix.Matrix;

/**
 * Created by assaad on 29/06/16.
 */
public class MatrixTest {
    public static void main(String[] arg) {
        final String csvdir = "/Users/assaad/work/github/data/X2.csv";

        Matrix X=Matrix.loadFromCsv(csvdir);
        print(X);
        System.out.println();

        Matrix Xinv = Matrix.pinv(X,false);
        print(Xinv);
        System.out.println();

        Matrix Y=Matrix.multiply(Xinv,X);

        print(Y);
        System.out.println();

    }

    public static void print(Matrix Y){
        for(int i=0;i<Y.rows();i++){
            for(int j=0;j<Y.columns();j++){
                System.out.print(Y.get(i,j)+" ");
            }
            System.out.println();
        }
    }
}