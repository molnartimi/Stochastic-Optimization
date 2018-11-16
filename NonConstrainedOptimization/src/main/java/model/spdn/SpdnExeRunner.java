package model.spdn;

import java.util.List;

import hu.bme.mit.inf.petridotnet.spdn.AnalysisBuilder;
import hu.bme.mit.inf.petridotnet.spdn.AnalysisConfiguration;
import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;
import hu.bme.mit.inf.petridotnet.spdn.Spdn;
import hu.bme.mit.inf.petridotnet.spdn.SpdnAnalyzer;
import model.ModelCheckerRunner;

public class SpdnExeRunner implements ModelCheckerRunner<SpdnAnalysisResult> {
	
	private SpdnAnalyzer analyzer;
	private AnalysisBuilder analysisBuilder;
	private List<Parameter> parameterList;
	private List<Reward> rewardList;
	
	public SpdnExeRunner(String filePath, List<Reward> rewardList, List<Parameter> parameterList) {
		Spdn spdn = new Spdn("../../SPDN");
		analyzer = spdn.openModel(filePath, AnalysisConfiguration.DEFAULT);
		analysisBuilder = analyzer.createAnalysisBuilder();
		
		this.parameterList = parameterList;
		this.rewardList = rewardList;
	}
	
	public void setTolerance(double tolerance) {
		analyzer.setTolerance(tolerance);	
	}
	
	public SpdnAnalysisResult run(List<Double> parameterValues) {
		if (parameterList.size() != parameterValues.size()) 
			throw new IllegalArgumentException("Size of parameter list must equal to size of value list.");
		
		for (Reward r: rewardList) {
			analysisBuilder.withReward(r);
		}
		
		for (int i = 0; i < parameterList.size(); i++) {
			analysisBuilder.withParameter(parameterList.get(i), parameterValues.get(i));
		}
		
		return new SpdnAnalysisResult(analysisBuilder.run());
	}

}
