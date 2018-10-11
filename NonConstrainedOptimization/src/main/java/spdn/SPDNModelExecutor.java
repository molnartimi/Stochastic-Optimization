package spdn;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.inf.petridotnet.spdn.AnalysisResult;
import hu.bme.mit.inf.petridotnet.spdn.Reward;

public class SPDNModelExecutor {
	
	private SPDNModel model;

	public SPDNModelExecutor(SPDNModel model) {
		this.model = model;
	}
	
	public List<Double> analyze(List<Double> variables) {
		AnalysisResult result = SpdnExeRunner.run(model.filePath, model.rewardList, model.parameterList, variables);
		List<Double> rewardResults = new ArrayList<>();
		for (Reward r: model.rewardList) {
			rewardResults.add(result.getValue(r));
		}
		return rewardResults;
	}
	
	public double calcObjective(List<Double> variables) {
		List<Double> rewardResults = analyze(variables);
		
		double objectiveResult = 0;
		for (int i = 0; i < model.getRewardNum(); i++) {
			objectiveResult += model.getSquareErrorOfReward(i, rewardResults.get(i));
		}
		
		return objectiveResult;
	}
}
