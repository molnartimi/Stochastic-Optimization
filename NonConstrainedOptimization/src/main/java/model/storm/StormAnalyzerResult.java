package model.storm;

import hu.bme.mit.modelchecker.storm.checker.AnalysisResult;
import hu.bme.mit.modelchecker.storm.model.ModelReward;
import model.ModelCheckerResult;

public class StormAnalyzerResult extends ModelCheckerResult {
	private final AnalysisResult result;
	
	public StormAnalyzerResult(AnalysisResult result) {
		this.result = result;
	}
	
	public Double getResultOf(ModelReward reward) {
		return result.getResultOf(reward);
	}

}
