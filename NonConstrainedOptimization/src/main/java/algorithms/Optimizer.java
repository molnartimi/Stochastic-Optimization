package algorithms;

import java.util.ArrayList;
import java.util.List;

import model.Model;
import model.ModelChecker;
import model.ModelCheckerResult;

public abstract class Optimizer<HyperParam extends HyperParameters> {
	protected ModelChecker<Model<?,?,?>, ModelCheckerResult> modelChecker;
	protected Model<?, ?, ?> model;
	
	public Optimizer(Model model) {
		this.model = model;
		modelChecker = model.getInstanceOfChecker();
	}
	
	public abstract OptimizerResult optimize(HyperParam hyperParams);
	
	public static List<Double> copyArray(List<Double> array) {
		ArrayList<Double> newArray = new ArrayList<Double>();
		array.forEach(d -> newArray.add(d));
		return newArray;
	}
}
