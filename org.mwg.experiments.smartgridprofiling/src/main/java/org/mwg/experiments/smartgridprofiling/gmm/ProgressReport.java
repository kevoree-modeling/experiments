package org.mwg.experiments.smartgridprofiling.gmm;
public interface ProgressReport {
    public void updateProgress(double progress);
    public boolean isCancelled();
}
