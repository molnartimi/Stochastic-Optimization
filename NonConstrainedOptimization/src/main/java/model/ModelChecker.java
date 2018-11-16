package model;

import java.util.List;

public abstract class ModelChecker<M extends Model<?,?,?>, R extends ModelCheckerResult> {
	protected M model;
	protected ModelCheckerRunner<R> runner;

	public ModelChecker(M model) {
		this.model = model;
		initRunner();
	}
	
	public void setTolerance(double tolerance) {
		runner.setTolerance(tolerance);
	}
	
	public abstract List<Double> analyze(List<Double> variables);
	
	public double calcObjective(List<Double> variables) {
		List<Double> rewardResults = analyze(variables);
		
		double objectiveResult = 0;
		for (int i = 0; i < model.rewardSize(); i++) {
			objectiveResult += model.getSquareErrorOfReward(i, rewardResults.get(i));
		}
		
		return objectiveResult;
	}

	protected abstract void initRunner();
}
