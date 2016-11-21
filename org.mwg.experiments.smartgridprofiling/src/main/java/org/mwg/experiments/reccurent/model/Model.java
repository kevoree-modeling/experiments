package org.mwg.experiments.reccurent.model;
import java.io.Serializable;
import java.util.List;

import org.mwg.experiments.reccurent.matrix.Matrix;
import org.mwg.experiments.reccurent.autodiff.Graph;


public interface Model extends Serializable {
	Matrix forward(Matrix input, Graph g) throws Exception;
	void resetState();
	List<Matrix> getParameters();
}
