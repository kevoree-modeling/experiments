package org.mwg.experiments.LSTM;

import org.mwg.ml.common.matrix.MatrixOps;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.struct.Matrix;

import java.util.Random;

/**
 * Created by assaad on 21/11/2016.
 */
public class MatrixLSTM implements IAgentSupervised {


    private final int full_input_dimension;
    private final int output_dimension;
    private final int cell_blocks;

    private Matrix context;

    private Neuron F;
    private Neuron G;
    private NeuronType neuron_type_F = NeuronType.Sigmoid;
    private NeuronType neuron_type_G = NeuronType.Sigmoid;

    private Matrix weights;
    private Matrix weightsOut;

    //partials (Need this for each output? Need to remind myself..)
    private Matrix dS;


    private double init_weight_range = 0.1;
    private double SCALE_OUTPUT_DELTA = 1.0;
    public static double learningRate = 0.07;//0.07


    public MatrixLSTM(Random random, int input_dimension, int output_dimension, int cell_blocks) {
        this.output_dimension = output_dimension;
        this.cell_blocks = cell_blocks;
        this.full_input_dimension = input_dimension + cell_blocks + 1; //+1 for bias

        context = VolatileMatrix.empty(cell_blocks, 1);


        F = Neuron.Factory(neuron_type_F);
        G = Neuron.Factory(neuron_type_G);

        weights = VolatileMatrix.random(cell_blocks * 2, full_input_dimension, -init_weight_range, init_weight_range);
        dS = VolatileMatrix.empty(cell_blocks * 2, full_input_dimension);

        weightsOut = VolatileMatrix.random(output_dimension, cell_blocks + 1, -init_weight_range, init_weight_range);
    }


    @Override
    public void Reset() {
        for (int c = 0; c < context.rows(); c++)
            context.set(c, 0, 0.0);
        //reset accumulated partials
        for (int c = 0; c < cell_blocks * 2; c++) {
            for (int i = 0; i < full_input_dimension; i++) {
                this.dS.set(c, i, 0.0);
            }
        }
    }

    @Override
    public double[] Next(double[] input, double[] target_output) throws Exception {

        Matrix full_input = VolatileMatrix.empty(full_input_dimension, 1);

        //setup input vector


        int loc = 0;
        for (int i = 0; i < input.length; i++)
            full_input.set(loc++, 0, input[i]);
        for (int c = 0; c < context.rows(); c++)
            full_input.set(loc++, 0, context.get(c, 0));
        full_input.set(loc++, 0, 1.0); //bias

        //cell block arrays
        double[] actF = new double[cell_blocks];
        double[] actG = new double[cell_blocks];
        double[] actH = new double[cell_blocks];


        Matrix sum = MatrixOps.multiply(weights, full_input);


        for (int j = 0; j < cell_blocks; j++) {
            actF[j] = F.Activate(sum.get(j, 0));
            actG[j] = G.Activate(sum.get(j + cell_blocks, 0));
            actH[j] = actF[j] * context.get(j, 0) + (1 - actF[j]) * actG[j];
        }


        Matrix full_hidden = VolatileMatrix.empty(cell_blocks + 1, 1);

        //prepare hidden layer plus bias
        loc = 0;
        for (int j = 0; j < cell_blocks; j++)
            full_hidden.set(loc++, 0, actH[j]);
        full_hidden.set(loc++, 0, 1.0); //bias

        Matrix output= MatrixOps.multiply(weightsOut,full_hidden);

        //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////
        //BACKPROP
        //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////

        //scale partials
        for (int j = 0; j < cell_blocks; j++) {

            double f = actF[j];
            double df = F.Derivative(sum.get(j, 0));
            double g = actG[j];
            double dg = G.Derivative(sum.get(j + cell_blocks, 0));
            double h_ = context.get(j,0); //prev value of h

            for (int i = 0; i < full_input_dimension; i++) {

                double prevdSdF = dS.get(j,i);
                double prevdSdG =  dS.get(j+cell_blocks,i);
                double in = full_input.get(i,0);

                dS.set(j,i,((h_ - g) * df * in) + (f * prevdSdF));
                dS.set(j+cell_blocks,i,((1 - f) * dg * in) + (f * prevdSdG));
            }
        }

        if (target_output != null) {

            //output to hidden
            double[] deltaOutput = new double[output_dimension];
            double[] deltaH = new double[cell_blocks];
            for (int k = 0; k < output_dimension; k++) {
                deltaOutput[k] = (target_output[k] - output.get(k,0)) * SCALE_OUTPUT_DELTA;
                for (int j = 0; j < cell_blocks; j++) {
                    deltaH[j] += deltaOutput[k] * weightsOut.get(k,j);
                    weightsOut.add(k,j,deltaOutput[k] * actH[j] * learningRate);
                }
                //bias
                weightsOut.add(k,cell_blocks,deltaOutput[k] * 1.0 * learningRate);
            }

            //input to hidden
            for (int j = 0; j < cell_blocks; j++) {
                for (int i = 0; i < full_input_dimension; i++) {
                    weights.add(j,i, deltaH[j] * dS.get(j,i) * learningRate);
                    weights.add(j+cell_blocks,i, deltaH[j] * dS.get(j+cell_blocks,i) * learningRate);
                }
            }
        }

        //////////////////////////////////////////////////////////////

        //roll-over context to next time step
        for (int j = 0; j < cell_blocks; j++) {
            context.set(j,0, actH[j]);
        }

        //give results
        return output.data();
    }

    @Override
    public double[] Next(double[] input) throws Exception {
        return Next(input, null);
    }
}
