package algorithms.bayes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.HyperParameters;
import algorithms.Optimizer;
import algorithms.OptimizerResult;
import algorithms.Sample;
import algorithms.ToleranceExceededException;
import algorithms.helper.SimpleOptimizer;
import algorithms.helper.gd.GDHyperParam;
import algorithms.helper.gd.GDHyperParamBuilder;
import algorithms.helper.gd.GradientDescent;
import model.Model;

public class BayesianOptimization extends Optimizer<BayesHyperParam> {
	public static final String ID = "BAYE";
	protected static final Logger logger = LoggerFactory.getLogger(BayesianOptimization.class);
	protected double tolerance;
	
	protected GaussProcess gp;
	protected ExpectedImprovement ei;
	protected SimpleOptimizer<GDHyperParam> helperOpt;
	protected GDHyperParam helperOptHyperParams;

	public BayesianOptimization(Model model) {
		super(model);
	}

	@Override
	public OptimizerResult optimize(BayesHyperParam params) {
		logger.info("Start to optimize " + model.id);		
		long startTime = System.nanoTime();
		
		try {
			logger.info("Initializing Gauss Process with " + params.initSamples + " samples");
			initGaussProcess(params.initSamples, params.alpha0, params.alpha, params.tolerance);
			initAcquisitionFuncResources();
			
			for (int i = params.initSamples; i < params.maxSamples; i++) {
				List<Double> acqFunMax = optimizeAcquisitionFunction();
				gp.updateWithSample(new Sample<List<Double>>(acqFunMax, modelChecker.calcObjective(acqFunMax)));
			}
		} catch (ToleranceExceededException e) {
			logger.info("Tolerance border exceeded, stop optimization");
		}
		
		OptimizerResult result = new OptimizerResult(gp.getBestPosition().value, gp.getBestPosition().point, ID, params.getHyperParams(), model);
		result.setTime(System.nanoTime() - startTime);
		return result;
	}
	
	/**
	 * Initializes GP with initSamples samples, alpha hyperparameters for Gaussian kernel function
	 * @param initSamples
	 * @param alpha 
	 * @param alpha0
	 * @param tolerance 
	 * @throws ToleranceExceededException
	 */
	private void initGaussProcess(int initSamples, double alpha0, List<Double> alpha, double tolerance) throws ToleranceExceededException {
		gp = new GaussProcess(alpha0, alpha, tolerance);
		for (List<Double> point: model.latinHypercubeParamValues(initSamples)) {
			double value = modelChecker.calcObjective(point);
			gp.updateWithSample(new Sample<List<Double>>(point, value));
		}
	}
	
	private void initAcquisitionFuncResources() {
		ei = new ExpectedImprovement(gp);
		helperOpt = new GradientDescent(model);
		helperOptHyperParams = new GDHyperParamBuilder(model).restart(3).build();
	}

	/**
	 * Optimize acquisition function
	 * @return
	 */
	private List<Double> optimizeAcquisitionFunction() {
		Sample<RealVector> bestPoint = helperOpt.optimize(ei::calc, ei::calcDx, helperOptHyperParams);
		List<Double> result = new ArrayList<Double>();
		for (int i = 0; i < bestPoint.point.getDimension(); i++) {
			result.add(bestPoint.point.getEntry(i));
		}
		return result;
	}
}
