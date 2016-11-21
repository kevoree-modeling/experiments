package org.mwg.experiments.reccurent.model;

import java.io.Serializable;


public interface Nonlinearity extends Serializable {
	double forward(double x);
	double backward(double x);
}
