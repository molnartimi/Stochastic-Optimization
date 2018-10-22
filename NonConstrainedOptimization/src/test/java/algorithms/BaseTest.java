package algorithms;

public class BaseTest {
	final String separator = "\n-----------------------------\n";
	
	public <Params extends HyperParameters> void optimize(Optimizer<Params> opt, Params params) {
		System.out.println(separator + opt.optimize(params).toString() + separator);
	}
}
