package algorithms.bayes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.Optimizer;
import algorithms.OptimizerResult;
import algorithms.Sample;
import algorithms.ToleranceExceededException;
import model.Model;

public class BayesianOptimization extends Optimizer<BayesHyperParam> {
	public static final String ID = "BAYE";
	protected static final Logger logger = LoggerFactory.getLogger(BayesianOptimization.class);
	protected double tolerance;
	protected Sample bestPosition;

	public BayesianOptimization(Model model) {
		super(model);
	}

	@Override
	public OptimizerResult optimize(BayesHyperParam params) {
		logger.info("Start to optimize " + model.id);
		long startTime = System.nanoTime();
		tolerance = params.tolerance;
		
		try {
			logger.info("Initializing Gauss Process with " + params.initSamples + " samples");
			initGaussProcess(params.initSamples);
			
			for (int i = params.initSamples; i < params.maxSamples; i++) {
				updateGaussProcess();
				List<Double> acqFunMax = optimizeAcquisitionFunction();
				calculateObjctive(acqFunMax);
			}
		} catch (ToleranceExceededException e) {
			logger.info("Tolerance border exceeded, stop optimization");
		}
		
		OptimizerResult result = new OptimizerResult(bestPosition.value, bestPosition.point, ID, params.getHyperParams(), model);
		result.setTime(System.nanoTime() - startTime);
		return result;
	}

	private void calculateObjctive(List<Double> acqFunMax) throws ToleranceExceededException {
		double value = modelChecker.calcObjective(acqFunMax);
		if (value < tolerance) throw new ToleranceExceededException();
		// TODO Auto-generated method stub
	}

	private List<Double> optimizeAcquisitionFunction() {
		// TODO Auto-generated method stub
		return null;
	}

	private void updateGaussProcess() {
		// TODO Auto-generated method stub
		
	}

	private void initGaussProcess(int initSamples) throws ToleranceExceededException {
		// TODO Auto-generated method stub
		
	}

}
