package org.mwg.experiments.reccurent.datastructs;
import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.mwg.experiments.reccurent.loss.Loss;
import org.mwg.experiments.reccurent.model.Model;
import org.mwg.experiments.reccurent.model.Nonlinearity;

public abstract class DataSet implements Serializable {
	public int inputDimension;
	public int outputDimension;
	public Loss lossTraining;
	public Loss lossReporting;
	public List<DataSequence> training;
	public List<DataSequence> validation;
	public List<DataSequence> testing;
	public abstract void DisplayReport(Model model, Random rng) throws Exception;
	public abstract Nonlinearity getModelOutputUnitToUse();
}
