import org.mwg.experiments.LSTM.ComplexLSTM;
import org.mwg.experiments.LSTM.DistractedSequenceRecall;
import org.mwg.experiments.LSTM.MatrixLSTM;
import org.mwg.experiments.LSTM.SimpleLSTM;

import java.util.Random;

public class Test {
	public static void main(String[] args) throws Exception {
		
		System.out.println("Test of SimpleLSTM\n");
		
		Random r = new Random(1234);
		DistractedSequenceRecall task = new DistractedSequenceRecall(r);

		int cell_blocks = 15;
		//SimpleLSTM slstm = new SimpleLSTM(r, task.GetObservationDimension(), task.GetActionDimension(), cell_blocks);

		//ComplexLSTM slstm = new ComplexLSTM(r, task.GetObservationDimension(), task.GetActionDimension(), cell_blocks);
		MatrixLSTM slstm = new MatrixLSTM(r, task.GetObservationDimension(), task.GetActionDimension(), cell_blocks);

		long timestart, timeend;

		timestart=System.currentTimeMillis();
		for (int epoch = 0; epoch < 5000; epoch++) {
			double fit = task.EvaluateFitnessSupervised(slstm);

			if (epoch % 10 == 0){
				timeend=System.currentTimeMillis();
				double x=timeend-timestart;
				x=x/(epoch+1);
				System.out.println("["+epoch+"] error = " + (1 - fit)+" rate: "+x+" ms/epoch");
			}
		}
		System.out.println("done.");
	}

}
