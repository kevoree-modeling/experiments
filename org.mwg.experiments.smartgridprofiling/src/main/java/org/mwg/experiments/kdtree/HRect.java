package org.mwg.experiments.kdtree;


// Hyper-Rectangle class supporting KDTree class


class HRect {

    protected double[] min;
    protected double[] max;


    protected HRect(double[] vmin, double[] vmax) {

        min = new double[vmin.length];
        max = new double[vmax.length];
        
        System.arraycopy(vmin,0,min,0,vmin.length);
        System.arraycopy(vmax,0,max,0,vmax.length);
    }

    protected Object clone() {
        return new HRect(min, max);
    }

    // from Moore's eqn. 6.6
    protected double[] closest(double[] t) {

        double[] p = new double[t.length];

        for (int i=0; i<t.length; ++i) {
            if (t[i]<=min[i]) {
                p[i] = min[i];
            }
            else if (t[i]>=max[i]) {
                p[i] = max[i];
            }
            else {
                p[i] = t[i];
            }
        }

        return p;
    }

    // used in initial conditions of KDTree.nearest()
    protected static HRect infiniteHRect(int d) {

        double[] vmin = new double[d];
        double[] vmax = new double[d];

        for (int i=0; i<d; ++i) {
            vmin[i] = Double.NEGATIVE_INFINITY;
            vmax[i] = Double.POSITIVE_INFINITY;
        }

        return new HRect(vmin, vmax);
    }

    // currently unused
    protected HRect intersection(HRect r) {

        double[] newmin = new double[min.length];
        double[] newmax = new double[min.length];

        for (int i=0; i<min.length; ++i) {
            newmin[i] = Math.max(min[i], r.min[i]);
            newmax[i] = Math.min(max[i], r.max[i]);
            if (newmin[i] >= newmax[i]) return null;
        }

        return new HRect(newmin, newmax);
    }

    // currently unused
    protected double area () {

        double a = 1;

        for (int i=0; i<min.length; ++i) {
            a *= (max[i] - min[i]);
        }

        return a;
    }

    public String toString() {
        return min + "\n" + max + "\n";
    }
}

