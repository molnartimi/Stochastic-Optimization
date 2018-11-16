package algorithms;

import model.Model;
import model.ModelChecker;
import model.ModelCheckerResult;

public abstract class Optimizer<HyperParam extends HyperParameters> {
	protected ModelChecker<Model<?,?,?>, ModelCheckerResult> modelChecker;
	protected Model model;
	
	public Optimizer(Model model) {
		this.model = model;
		modelChecker = model.getInstanceOfChecker();
	}
	
	public abstract OptimizerResult optimize(HyperParam hyperParams);
}
