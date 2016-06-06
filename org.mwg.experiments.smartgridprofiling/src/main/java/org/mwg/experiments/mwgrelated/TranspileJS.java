package org.mwg.experiments.mwgrelated;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by assaad on 06/06/16.
 */
public class TranspileJS {
    public static void main(String [] arg){
        try {
            String fname="dgesdd";
            String s = "String jobz, int m, int n, double[] data, int lda, double[] s, double[] u, int ldu, double[] vt, int ldvt, double[] work, int length, int[] iwork, int[] info";
            String[] res = s.split(",");

            PrintWriter pw = new PrintWriter(new File(fname+".txt"));
            String generate = "var ";

            ArrayList<String> parsedType = new ArrayList<>();
            ArrayList<String> parsedOriginal = new ArrayList<>();
            ArrayList<String> parsedNew = new ArrayList<>();

            //First step parsing
            for (String x : res) {
                String[] temp = x.trim().split(" ");
                String type = temp[0].toLowerCase();
                String varOld = temp[1];
                String var = "p" + temp[1];

                parsedType.add(type);
                parsedOriginal.add(varOld);
                parsedNew.add(var);


                if (type.equals("transposetype")) {
                    generate += var + " = this.netlib._malloc(1),\n";
                } else if (type.equals("int")) {
                    generate += var + " = this.netlib._malloc(4),\n";
                } else if (type.equals("double")) {
                    generate += var + " = this.netlib._malloc(8),\n";
                } else if (type.equals("int[]")) {
                    generate += var + " = this.netlib._malloc(4 * " + varOld + ".length),\n";
                } else if (type.equals("double[]")) {
                    generate += var + " = this.netlib._malloc(8 * " + varOld + ".length),\n";
                } else if (type.equals("string")) {
                    generate += var + " = this.netlib._malloc(1),\n";
                }


            }

            generate=generate.substring(0,generate.length() - 2);
            generate += ";\n\n\n";


            //Second step copy variables
            for(int i=0;i<parsedType.size();i++){
                String type=parsedType.get(i);
                String originalVar=parsedOriginal.get(i);
                String newVar=parsedNew.get(i);

                if (type.equals("transposetype")) {
                    generate +=   "this.netlib.setValue("+newVar+", org.mwg.ml.common.matrix.blassolver.blas.BlasHelper.transTypeToChar("+originalVar+").charCodeAt(0), 'i8');\n";
                } else if (type.equals("int")) {
                    generate+="this.netlib.setValue("+newVar+", "+originalVar+", 'i32');\n";
                } else if (type.equals("double")) {
                    generate+="this.netlib.setValue("+newVar+", "+originalVar+", 'double');\n";
                } else if (type.equals("int[]")) {
                    generate+= "\nvar ii"+newVar+" = new Int32Array(this.netlib.HEAP32.buffer,"+newVar+", "+originalVar+".length);\n";
                    generate+="ii"+newVar+".set("+originalVar+");\n\n";
                } else if (type.equals("double[]")) {
                    generate+= "\nvar dd"+newVar+" = new Float64Array(this.netlib.HEAPF64.buffer,"+newVar+", "+originalVar+".length);\n";
                    generate+="dd"+newVar+".set("+originalVar+");\n\n";
                }else if (type.equals("string")) {
                    generate+="this.netlib.setValue("+newVar+", "+originalVar+".charCodeAt(0), 'i8');\n";
                }
            }
            generate += "\n\n\n";


            //Third step function call
            generate+= "var "+fname+" = this.netlib.cwrap('f2c_"+fname+"', null, [";
            for(int i=0;i<parsedNew.size();i++){
                generate+="'number',";
            }
            generate=generate.substring(0,generate.length() - 1);
            generate+="]);\n";
            generate+=fname+"(";
            for(int i=0;i<parsedNew.size();i++){
                generate+=parsedNew.get(i)+", ";
            }
            generate=generate.substring(0,generate.length() - 2);
            generate+=");\n";
            generate += "\n\n\n";

            //Fourth step set back variables

            for(int i=0;i<parsedNew.size();i++){
                if(parsedType.get(i).equals("double[]")){
                    generate+=parsedOriginal.get(i)+".set("+"dd"+parsedNew.get(i)+");\n";
                }
                else if(parsedType.get(i).equals("int[]")){
                    generate+=parsedOriginal.get(i)+".set("+"ii"+parsedNew.get(i)+");\n";
                }
            }

            generate += "\n\n\n";

            for(int i=0;i<parsedNew.size();i++){
                generate+=" this.netlib._free("+parsedNew.get(i)+");\n";
            }

            pw.print(generate);
            pw.flush();
            pw.close();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }


    }
}
