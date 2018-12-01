package model.storm;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.modelchecker.storm.model.ModelReward;
import model.ModelChecker;

public class StormAnalyzer extends ModelChecker<StormModel, StormAnalyzerResult> {
	
	
	public StormAnalyzer(StormModel model) {
		super(model);
		
	}

	@Override
	public List<Double> analyze(List<Double> variables) {
		StormAnalyzerResult result = this.runner.run(variables);
		List<Double> resultList = new ArrayList<>();
		for (ModelReward reward: model.simpleRewardList) {
			resultList.add(result.getResultOf(reward));
		}
		return resultList;
	}

	@Override
	protected void initRunner() {
		this.runner = new StormCLIRunner(model);
		
	}

}
