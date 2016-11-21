package org.mwg.experiments.reccurent.loss;

import java.io.Serializable;

import org.mwg.experiments.reccurent.matrix.Matrix;

public interface Loss extends Serializable {
	void backward(Matrix actualOutput, Matrix targetOutput) throws Exception;
	double measure(Matrix actualOutput, Matrix targetOutput) throws Exception;
}
