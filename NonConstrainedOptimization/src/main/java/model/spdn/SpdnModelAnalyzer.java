package model.spdn;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.inf.petridotnet.spdn.Reward;
import model.ModelChecker;

public class SpdnModelAnalyzer extends ModelChecker<SpdnModel, SpdnAnalysisResult> {

	public SpdnModelAnalyzer(SpdnModel model) {
		super(model);
	}
	
	public List<Double> analyze(List<Double> variables) {
		SpdnAnalysisResult result = runner.run(variables);
		List<Double> rewardResults = new ArrayList<>();
		for (Reward r: model.simpleRewardList) {
			rewardResults.add(result.getValue(r));
		}
		return rewardResults;
	}

	@Override
	protected void initRunner() {
		this.runner = new SpdnExeRunner(model.filePath, model.simpleRewardList, model.simpleParameterList);
	}
}
