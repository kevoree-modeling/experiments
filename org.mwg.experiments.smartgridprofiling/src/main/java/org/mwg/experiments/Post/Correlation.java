package org.mwg.experiments.Post;

/**
 * Created by assaad on 26/05/16.
 */
public class Correlation {
    private double[] sumX;
    private double[][] sumSqX;
    private double[] sumY;
    private double[] sumSqY;
    private int[] total;


    public Correlation(int input) {
        sumX = new double[input];
        sumSqX = new double[input][2];
        sumSqY = new double[input];
        sumY = new double[input];
        total = new int[input];
    }

    public void feedOne(int i, double x, double y) {
        sumX[i] += x;
        sumSqX[i][0] += x * x;
        sumSqX[i][1] += x * y;

        sumY[i] += y;
        sumSqY[i] += y * y;
        total[i]++;
    }

    public void feed(double[] x, double y) {
        for (int i = 0; i < x.length; i++) {
            feedOne(i,x[i],y);
        }
    }

    public double[] getCorrelation() {
        int features = sumX.length;
        double[] res = new double[features];

        for (int i = 0; i < features; i++) {
            if (total[i] > 1) {
                try {
                    res[i] = (total[i] * sumSqX[i][1] - sumX[i] * sumY[i]) / (Math.sqrt(total[i] * sumSqX[i][0] - (sumX[i] * sumX[i])) * Math.sqrt(total[i] * sumSqY[i] - (sumY[i] * sumY[i])));
                } catch (Exception ex) {
                    res[i] = 0;
                }

            }
        }
        return res;
    }

    public int getMaxArg(double[] correlation) {
        int x = 0;
        double max = Double.MIN_VALUE;
        for (int j = 0; j < correlation.length; j++) {
            if (correlation[j] > max) {
                max = correlation[j];
                x = j;
            }
        }
        return x;
    }

}
