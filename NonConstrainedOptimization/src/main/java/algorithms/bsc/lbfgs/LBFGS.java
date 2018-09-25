package algorithms.bsc.lbfgs;

import org.apache.commons.math3.linear.MatrixUtils;

import algorithms.Optimizer;
import hu.bme.mit.inf.optimization.wrapper.breeze.LbfgsWrapper;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;


public class LBFGS extends Optimizer<LBFGSHyperParam> {
	public static final String ID = "BFGS";

	public LBFGS(Model model) {
		super(model);
	}

	public SPDNResult optimize(LBFGSHyperParam params) {
		long startTime = System.nanoTime();

		
		double[] minPoint = new double[model.getDim()];
		double minValue = 10000000;
		int iter = 0;
		double[] xn = params.initPoint;
		
		LbfgsWrapper lbfgs = new LbfgsWrapper(params.maxIter, params.m, params.tolerance);
		while (iter < params.restart+1 && minValue > params.tolerance) {
			try {
				double[] result = lbfgs.minimize(spdn, xn);
				
				double value = spdn.f(result);
				if (minPoint.length == 0 || value < minValue) {
					minValue = value;
					minPoint = result;
				}
			} catch (SpdnException e) {
				// Nothing to do, go and try again.
			}
			iter++;
			xn = model.getRandomPoint();
		}
		
		SPDNResult result = new SPDNResult(minValue, 
											SPDN.convertPoint(MatrixUtils.createRealVector(minPoint)).toArray(), 
											ID, 
											params.getHyperParams(), 
											model);
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
}
