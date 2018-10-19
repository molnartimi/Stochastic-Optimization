package algorithms;

import model.TestModel;
import spdn.SpdnModel;

public class BaseTest {
	final String separator = "\n-----------------------------\n";
	public final SpdnModel model = TestModel.SMPL.model();
	
	public <Params extends HyperParameters> void optimize(Optimizer<Params> opt, Params params) {
		System.out.println(separator + opt.optimize(params).toString() + separator);
	}
}
