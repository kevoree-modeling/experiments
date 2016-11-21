import java.util.Random;
import com.evolvingstuff.DistractedSequenceRecall;
import com.evolvingstuff.SimpleLSTM;

public class Test {
	public static void main(String[] args) throws Exception {
		
		System.out.println("Test of SimpleLSTM\n");
		
		Random r = new Random(1234);
		DistractedSequenceRecall task = new DistractedSequenceRecall(r);

		int cell_blocks = 15;
		SimpleLSTM slstm = new SimpleLSTM(r, task.GetObservationDimension(), task.GetActionDimension(), cell_blocks);
		
		for (int epoch = 0; epoch < 5000; epoch++) {
			double fit = task.EvaluateFitnessSupervised(slstm);
			if (epoch % 10 == 0)
				System.out.println("["+epoch+"] error = " + (1 - fit));
		}
		System.out.println("done.");
	}

}
