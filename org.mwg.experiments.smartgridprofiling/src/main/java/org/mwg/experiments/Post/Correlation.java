package org.mwg.experiments.Post;

/**
 * Created by assaad on 26/05/16.
 */
public class Correlation {
    private double[] sumX;
    private double[][] sumSqX;
    private double sumY;
    private double sumSqY;
    private int total;


    public Correlation(int input) {
        sumX = new double[input];
        sumSqX = new double[input][2];
        sumSqY = 0;
        sumY = 0;
        total = 0;
    }

    public void feed(double[] x, double y) {
        for (int i = 0; i < x.length; i++) {
            sumX[i] += x[i];
            sumSqX[i][0] += x[i] * x[i];
            sumSqX[i][1] += x[i] * y;
        }
        sumY += y;
        sumSqY += y * y;
        total++;
    }

    public double[] getCorrelation() {
        int features = sumX.length;
        double[] res = new double[features];

        for (int i = 0; i < features; i++) {
            if (total > 1) {
                try {
                    res[i] = (total * sumSqX[i][1] - sumX[i] * sumY) / (Math.sqrt(total*sumSqX[i][0]-(sumX[i]*sumX[i]))*Math.sqrt(total*sumSqY-(sumY*sumY)));
                }
                catch (Exception ex){
                    res[i]=0;
                }

            }
        }
        return res;
    }

    public int getMaxArg(double[] correlation){
        int x=0;
        double max=Double.MIN_VALUE;
        for(int j=0;j<correlation.length;j++){
            if(correlation[j]>max){
                max=correlation[j];
                x=j;
            }
        }
        return x;
    }

}
