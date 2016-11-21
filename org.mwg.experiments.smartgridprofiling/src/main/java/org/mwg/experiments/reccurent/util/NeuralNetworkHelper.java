package org.mwg.experiments.reccurent.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mwg.experiments.reccurent.model.FeedForwardLayer;
import org.mwg.experiments.reccurent.model.GruLayer;
import org.mwg.experiments.reccurent.model.LinearLayer;
import org.mwg.experiments.reccurent.model.LstmLayer;
import org.mwg.experiments.reccurent.model.Model;
import org.mwg.experiments.reccurent.model.NeuralNetwork;
import org.mwg.experiments.reccurent.model.Nonlinearity;
import org.mwg.experiments.reccurent.model.RnnLayer;

public class NeuralNetworkHelper {
	
	public static NeuralNetwork makeLstm(int inputDimension, int hiddenDimension, int hiddenLayers, int outputDimension, Nonlinearity decoderUnit, double initParamsStdDev, Random rng) {
		List<Model> layers = new ArrayList<>();
		for (int h = 0; h < hiddenLayers; h++) {
			if (h == 0) {
				layers.add(new LstmLayer(inputDimension, hiddenDimension, initParamsStdDev, rng));
			}
			else {
				layers.add(new LstmLayer(hiddenDimension, hiddenDimension, initParamsStdDev, rng));
			}
		}
		layers.add(new FeedForwardLayer(hiddenDimension, outputDimension, decoderUnit, initParamsStdDev, rng));
		return new NeuralNetwork(layers);
	}
	
	public static NeuralNetwork makeLstmWithInputBottleneck(int inputDimension, int bottleneckDimension, int hiddenDimension, int hiddenLayers, int outputDimension, Nonlinearity decoderUnit, double initParamsStdDev, Random rng) {
		List<Model> layers = new ArrayList<>();
		layers.add(new LinearLayer(inputDimension, bottleneckDimension, initParamsStdDev, rng));
		for (int h = 0; h < hiddenLayers; h++) {
			if (h == 0) {
				layers.add(new LstmLayer(bottleneckDimension, hiddenDimension, initParamsStdDev, rng));
			}
			else {
				layers.add(new LstmLayer(hiddenDimension, hiddenDimension, initParamsStdDev, rng));
			}
		}
		layers.add(new FeedForwardLayer(hiddenDimension, outputDimension, decoderUnit, initParamsStdDev, rng));
		return new NeuralNetwork(layers);
	}
	
	public static NeuralNetwork makeFeedForward(int inputDimension, int hiddenDimension, int hiddenLayers, int outputDimension, Nonlinearity hiddenUnit, Nonlinearity decoderUnit, double initParamsStdDev, Random rng) {
		List<Model> layers = new ArrayList<>();
		if (hiddenLayers == 0) {
			layers.add(new FeedForwardLayer(inputDimension, outputDimension, decoderUnit, initParamsStdDev, rng));
			return new NeuralNetwork(layers);
		}
		else {
			for (int h = 0; h < hiddenLayers; h++) {
				if (h == 0) {
					layers.add(new FeedForwardLayer(inputDimension, hiddenDimension, hiddenUnit, initParamsStdDev, rng));
				}
				else {
					layers.add(new FeedForwardLayer(hiddenDimension, hiddenDimension, hiddenUnit, initParamsStdDev, rng));
				}
			}
			layers.add(new FeedForwardLayer(hiddenDimension, outputDimension, decoderUnit, initParamsStdDev, rng));
			return new NeuralNetwork(layers);
		}
	}
	
	public static NeuralNetwork makeGru(int inputDimension, int hiddenDimension, int hiddenLayers, int outputDimension, Nonlinearity decoderUnit, double initParamsStdDev, Random rng) {
		List<Model> layers = new ArrayList<>();
		for (int h = 0; h < hiddenLayers; h++) {
			if (h == 0) {
				layers.add(new GruLayer(inputDimension, hiddenDimension, initParamsStdDev, rng));
			}
			else {
				layers.add(new GruLayer(hiddenDimension, hiddenDimension, initParamsStdDev, rng));
			}
		}
		layers.add(new FeedForwardLayer(hiddenDimension, outputDimension, decoderUnit, initParamsStdDev, rng));
		return new NeuralNetwork(layers);
	}
	
	public static NeuralNetwork makeRnn(int inputDimension, int hiddenDimension, int hiddenLayers, int outputDimension, Nonlinearity hiddenUnit, Nonlinearity decoderUnit, double initParamsStdDev, Random rng) {
		List<Model> layers = new ArrayList<>();
		for (int h = 0; h < hiddenLayers; h++) {
			if (h == 0) {
				layers.add(new RnnLayer(inputDimension, hiddenDimension, hiddenUnit, initParamsStdDev, rng));
			}
			else {
				layers.add(new RnnLayer(hiddenDimension, hiddenDimension, hiddenUnit, initParamsStdDev, rng));
			}
		}
		layers.add(new FeedForwardLayer(hiddenDimension, outputDimension, decoderUnit, initParamsStdDev, rng));
		return new NeuralNetwork(layers);
	}
}
