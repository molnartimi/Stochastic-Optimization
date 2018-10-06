package algorithms;

import models.Model;

public class BaseTest {
	final String separator = "\n-----------------------------\n";
	public final Model model = Model.SIMPLE_SERVER;
	
	public <Params extends HyperParameters> void optimize(Optimizer<Params> opt, Params params) {
		System.out.println(separator + opt.optimize(params).toString() + separator);
	}
}
