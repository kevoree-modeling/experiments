package org.mwg.experiments.kdtree;


import org.mwg.experiments.smartgridprofiling.utility.GaussianProfile;
import org.mwg.ml.common.distance.GaussianDistance;

public class GaussianTreeNode extends GaussianProfile {

    KDNodeSync root;

    public void setPrecisions(double[] precisions) {
        this.precisions = precisions;
    }

    double[] precisions;


    public GaussianTreeNode() {
    }


    public void internalLearn(final double[] values, final double[] features) {
        super.learn(values);

        if (root == null) {
            root = new KDNodeSync();
            root.setDistance(new GaussianDistance(precisions));
            root.setThreshold(1.001);

            GaussianProfile profile = new GaussianProfile();
            profile.learn(values);
            root.insert(features, profile);
        } else {
            Object result = root.nearestWithinDistance(features);
            if (result != null) {
                GaussianProfile profile = (GaussianProfile) result;
                profile.learn(values);
            } else {
                GaussianProfile profile = new GaussianProfile();
                profile.learn(values);
                root.insert(features, profile);
            }

        }
    }


    public int getNumNodes() {
        return root.getNum();
    }


    public double predictValue(double[] values) {


        double[] features = new double[values.length - 1];
        System.arraycopy(values, 0, features, 0, values.length - 1);
        if (root == null) {
            return 0;
        } else {
            Object result = root.nearestWithinDistance(features);
            if (result != null) {
                GaussianProfile profile = (GaussianProfile) result;
                double[] avg = profile.getAvg();
                Double res = avg[avg.length - 1];
                return res;
            } else {
                double[] avg = getAvg();
                Double res = avg[avg.length - 1];
                return res;
            }
        }

    }
}
