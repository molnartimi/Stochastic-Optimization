package spdn;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.inf.petridotnet.spdn.AnalysisResult;
import hu.bme.mit.inf.petridotnet.spdn.Reward;

public class SpdnModelAnalyzer {
	
	private SpdnModel model;
	private SpdnExeRunner runner;

	public SpdnModelAnalyzer(SpdnModel model) {
		this.model = model;
		this.runner = new SpdnExeRunner(model.filePath, model.simpleRewardList, model.simpleParameterList);
	}
	
	public void setTolerance(double tolerance) {
		runner.setTolerance(tolerance);
	}
	
	public List<Double> analyze(List<Double> variables) {
		AnalysisResult result = runner.run(variables);
		List<Double> rewardResults = new ArrayList<>();
		for (Reward r: model.simpleRewardList) {
			rewardResults.add(result.getValue(r));
		}
		return rewardResults;
	}
	
	public double calcObjective(List<Double> variables) {
		List<Double> rewardResults = analyze(variables);
		
		double objectiveResult = 0;
		for (int i = 0; i < model.rewardSize(); i++) {
			objectiveResult += model.getSquareErrorOfReward(i, rewardResults.get(i));
		}
		
		return objectiveResult;
	}
}
