package model.storm;

import java.io.IOException;
import java.util.List;

import hu.bme.mit.modelchecker.storm.checker.AnalysisBuilder;
import hu.bme.mit.modelchecker.storm.checker.AnalysisResult;
import hu.bme.mit.modelchecker.storm.checker.StormRunner;
import hu.bme.mit.modelchecker.storm.exception.StormException;
import hu.bme.mit.modelchecker.storm.model.InputModel;
import hu.bme.mit.modelchecker.storm.model.InputModelBuilder;
import hu.bme.mit.modelchecker.storm.model.ModelParam;
import hu.bme.mit.modelchecker.storm.model.ModelReward;
import model.ModelCheckerRunner;
import model.ModelParameter;

public class StormCLIRunner implements ModelCheckerRunner<StormAnalyzerResult> {
	private StormRunner cliRunner;
	private InputModel model;
	private double tolerance = 1e-6;
	
	public StormCLIRunner(StormModel model) {
		InputModelBuilder modelBuilder = new InputModelBuilder(model.filePath);
		for (ModelParam param: model.simpleParameterList) {
			modelBuilder = modelBuilder.withParam(param);
		}
		for (ModelReward reward: model.simpleRewardList) {
			modelBuilder = modelBuilder.withReward(reward);
		}
		this.model = modelBuilder.build();
	}
	
	public StormCLIRunner(String filePath, List<ModelParam> parameters, List<ModelReward> rewards) {
		InputModelBuilder modelBuilder = new InputModelBuilder(filePath);
		for (ModelParam param: parameters) {
			modelBuilder = modelBuilder.withParam(param);
		}
		for (ModelReward reward: rewards) {
			modelBuilder = modelBuilder.withReward(reward);
		}
		this.model = modelBuilder.build();
	}
	
	
	@Override
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
		
	}

	@Override
	public StormAnalyzerResult run(List<Double> parameterValues) {
		if (parameterValues.size() != model.getParams().size()) {
			throw new IllegalArgumentException("Size of parameter list must equal to size of value list.");
		}
		
		AnalysisBuilder builder = new AnalysisBuilder(this.model);
		
		for (int i = 0; i < parameterValues.size(); i++) {
			builder = builder.withParam(model.getParams().get(i), parameterValues.get(i));
		}
		for (ModelReward reward: model.getRewards()) {
			try {
				builder = builder.withReward(reward);
			} catch (StormException e) {
				e.printStackTrace();
			}
		}
		AnalysisResult result = null;
		try {
			result = builder.build().runSteadyStateCheck();
		} catch (IOException | StormException e) {
			e.printStackTrace();
		}
		return new StormAnalyzerResult(result);
	}
	
	// TODO delete/update it!!! needed for modelfactory...
	public StormAnalyzerResult run2(List<Double> parameterValues) throws StormException {
		if (parameterValues.size() != model.getParams().size()) {
			throw new IllegalArgumentException("Size of parameter list must equal to size of value list.");
		}
		
		AnalysisBuilder builder = new AnalysisBuilder(this.model);
		builder = builder.withTolerance(tolerance);
		
		for (int i = 0; i < parameterValues.size(); i++) {
			builder = builder.withParam(model.getParams().get(i), parameterValues.get(i));
		}
		for (ModelReward reward: model.getRewards()) {
			builder = builder.withReward(reward);
		}
		AnalysisResult result = null;
		try {
			result = builder.build().runSteadyStateCheck();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new StormAnalyzerResult(result);
	}

}
