package org.mwg.experiments.mwgrelated;

import org.mwg.ml.common.matrix.Matrix;
import org.mwg.ml.common.matrix.TransposeType;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by assaad on 29/06/16.
 */
public class MatrixTest {
    public static void main(String[] arg) {
        final  String csvdir = "/Users/assaad/work/github/data/X.csv";

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvdir));
            String line;
            String[] data;
            Matrix X=new Matrix(null,336,8);

            int i=0;
            while ((line = br.readLine()) != null) {
                line = line.replace('"', ' ');
                data = line.split(",");
                int j=0;
                for(String k: data){
                    double d=Double.parseDouble(k);
                    X.set(i,j,d);
                    j++;
                }
                i++;
            }

            Matrix Y=Matrix.multiplyTranspose(TransposeType.TRANSPOSE, X, TransposeType.NOTRANSPOSE, X);



            Matrix inv = Matrix.pinv(Y,false);
            for(i=0; i<inv.rows();i++){
                System.out.println(inv.get(i,i));
            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}