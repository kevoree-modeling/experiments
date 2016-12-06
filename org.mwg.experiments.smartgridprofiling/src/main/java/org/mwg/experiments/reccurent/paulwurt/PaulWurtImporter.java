package org.mwg.experiments.reccurent.paulwurt;

import org.mwg.experiments.reccurent.datastructs.DataSequence;
import org.mwg.experiments.reccurent.datastructs.DataSet;
import org.mwg.experiments.reccurent.datastructs.DataStep;
import org.mwg.experiments.reccurent.loss.LossMultiDimensionalBinary;
import org.mwg.experiments.reccurent.loss.LossSumOfSquares;
import org.mwg.experiments.reccurent.model.Model;
import org.mwg.experiments.reccurent.model.Nonlinearity;
import org.mwg.experiments.reccurent.model.SigmoidUnit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Created by assaad on 05/12/2016.
 */
public class PaulWurtImporter extends DataSet {

    Random random;

    public PaulWurtImporter(String path, int trainingPercent, int testingPercent, Random rng) throws Exception {
        try {

            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            line = br.readLine(); //read header

            HashMap<Integer, DataSequence> dictionary = new HashMap<>();


            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] inputs = line.split("/");
                int castId = Integer.valueOf(inputs[0]);

                String[] inputVecStr = inputs[1].split(",");
                double[] inputVec = new double[inputVecStr.length];
                for (int i = 0; i < inputVec.length; i++) {
                    inputVec[i] = Double.valueOf(inputVecStr[i]);
                }

                String[] outputVecStr = inputs[2].split(",");
                double[] outputVec = new double[outputVecStr.length];
                for (int i = 0; i < outputVec.length; i++) {
                    outputVec[i] = Double.valueOf(outputVecStr[i]);
                }

                if (inputDimension == 0) {
                    inputDimension = inputVec.length;
                    outputDimension = outputVec.length;
                }

                DataStep dataStep = new DataStep(inputVec, outputVec);

                DataSequence dataSequence = dictionary.computeIfAbsent(castId, k -> new DataSequence());
                dataSequence.steps.add(dataStep);
            }

            this.random = rng;

            Object[] keys = dictionary.keySet().toArray();

            int[] order = new int[keys.length];
            for (int i = 0; i < order.length; i++) {
                order[i] = i;
            }
            for (int i = 0; i < order.length; i++) {
                int x = random.nextInt(order.length);
                int temp = order[x];
                order[x] = order[i];
                order[i] = temp;
            }

            int traininglim = keys.length * trainingPercent / 100;
            int testinglim = traininglim + keys.length * testingPercent / 100;

            training = new ArrayList<>();
            testing = new ArrayList<>();
            validation = new ArrayList<>();
            for (int i = 0; i < traininglim; i++) {
                training.add(dictionary.get(keys[order[i]]));
            }
            for (int i = traininglim; i < testinglim; i++) {
                testing.add(dictionary.get(keys[order[i]]));
            }
            for (int i = testinglim; i < keys.length; i++) {
                validation.add(dictionary.get(keys[order[i]]));
            }

            lossTraining = new LossSumOfSquares();
            lossReporting = new LossSumOfSquares();




        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public void DisplayReport(Model model, Random rng) throws Exception {

    }

    @Override
    public Nonlinearity getModelOutputUnitToUse() {
        return new SigmoidUnit();
    }
}
