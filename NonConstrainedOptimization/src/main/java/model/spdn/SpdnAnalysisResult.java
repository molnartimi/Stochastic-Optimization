package model.spdn;

import hu.bme.mit.inf.petridotnet.spdn.AnalysisResult;
import hu.bme.mit.inf.petridotnet.spdn.Reward;
import model.ModelCheckerResult;

public class SpdnAnalysisResult extends ModelCheckerResult {
	private final AnalysisResult result;
	
	public SpdnAnalysisResult(AnalysisResult result) {
		this.result = result;
	}
	
	public Double getValue(Reward reward) {
		return result.getValue(reward);
	}
}
