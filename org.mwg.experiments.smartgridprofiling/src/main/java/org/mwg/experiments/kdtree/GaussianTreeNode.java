package org.mwg.experiments.kdtree;


import org.mwg.Callback;
import org.mwg.experiments.smartgridprofiling.utility.GaussianProfile;
import org.mwg.ml.common.distance.GaussianDistance;

public class GaussianTreeNode extends GaussianProfile {

    KDNode root;

    public void setPrecisions(double[] precisions) {
        this.precisions = precisions;
    }

    double[] precisions;



    public GaussianTreeNode() {
    }




    public void internalLearn(final double[] values, final double[] features, final Callback<Boolean> callback) {
        super.learn(values);

        if (root == null) {
            root = new KDNode();
            root.setDistance(new GaussianDistance(precisions));
            root.setThreshold(1.001);

            GaussianProfile profile = new GaussianProfile();
            profile.learn(values);
            root.insert(features, profile, new Callback<Boolean>() {
                @Override
                public void on(Boolean result) {
                    callback.on(true);
                }
            });
        } else {
            root.nearestWithinDistance(features, new Callback<Object>() {
                @Override
                public void on(Object result) {
                    if (result != null) {
                        GaussianProfile profile = (GaussianProfile) result;
                        profile.learn(values);
                        if (callback != null) {
                            callback.on(true);
                        }
                    } else {
                        GaussianProfile profile = new GaussianProfile();
                        profile.learn(values);
                        root.insert(features, profile, new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {
                                if (callback != null) {
                                    callback.on(true);
                                }
                            }
                        });
                    }

                }
            });
        }
    }


    public int getNumNodes(){
        return root.getNum();
    }


    public void predictValue(double[] values, Callback<Double> callback){
        if(callback==null){
            return;
        }

        double[] features = new double[values.length - 1];
        System.arraycopy(values, 0, features, 0, values.length - 1);
        if(root==null){
            callback.on(null);
            return;
        }
        else {
            root.nearestWithinDistance(features, new Callback<Object>() {
                @Override
                public void on(Object result) {
                    if (result != null) {
                        GaussianProfile profile = (GaussianProfile) result;
                        double[] avg = profile.getAvg();
                        Double res = avg[avg.length - 1];
                        callback.on(res);
                    }
                    else {
                        double[] avg=getAvg();
                        Double res= avg[avg.length-1];
                        callback.on(res);
                    }
                }
            });

        }
    }
}