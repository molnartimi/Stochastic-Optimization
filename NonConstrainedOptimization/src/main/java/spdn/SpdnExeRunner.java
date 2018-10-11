package spdn;

import java.util.List;

import hu.bme.mit.inf.petridotnet.spdn.AnalysisBuilder;
import hu.bme.mit.inf.petridotnet.spdn.AnalysisConfiguration;
import hu.bme.mit.inf.petridotnet.spdn.AnalysisResult;
import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;
import hu.bme.mit.inf.petridotnet.spdn.Spdn;
import hu.bme.mit.inf.petridotnet.spdn.SpdnAnalyzer;

public class SpdnExeRunner {
	
	public static AnalysisResult run(String filePath, List<Reward> rewardList, List<Parameter> parameterList, List<Double> parameterValues) {
		if (parameterList.size() != parameterValues.size()) 
			throw new IllegalArgumentException("Size of parameter object list and size of value list must be equal.");
		
		Spdn spdn = new Spdn("../../SPDN");
		SpdnAnalyzer analyzer = spdn.openModel(filePath, AnalysisConfiguration.DEFAULT);
		AnalysisBuilder builder = analyzer.createAnalysisBuilder();
		
		for (Reward r: rewardList) {
			builder.withReward(r);
		}
		
		for (int i = 0; i < parameterList.size(); i++) {
			builder = builder.withParameter(parameterList.get(i), parameterValues.get(i));
		}
		
		return builder.run();
	}

}
