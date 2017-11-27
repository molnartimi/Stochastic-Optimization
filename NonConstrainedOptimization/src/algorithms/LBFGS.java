package algorithms;
import org.apache.commons.math3.linear.MatrixUtils;

import hu.bme.mit.inf.optimization.wrapper.breeze.LbfgsWrapper;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;


public class LBFGS {
	public static final String ID = "BFGS";
	
	private SPDN spdn;
	private Model model;
	
	public LBFGS(Model model) {
		this.model = model;
		this.spdn = new SPDN(model,0);
	}

	public SPDNResult optimize(int m, int maxIter, double tolerance, double[] initPoint, int restart) {
		m = m > 0 ? m : 4; 
		maxIter = maxIter > 0 ? maxIter : 20;
		tolerance = tolerance > 0 ? tolerance : 0.001;
		
		double[] minPoint = new double[0];
		double minValue = tolerance + 1;
		int iter = 0;
		double[] xn = initPoint.length > 0 ? initPoint : model.getRandomPoint();
		
		LbfgsWrapper lbfgs = new LbfgsWrapper(maxIter,m,tolerance);
		while (iter < restart+1 && minValue > tolerance) {
			double[] result = lbfgs.minimize(spdn, xn);
			
			double value = spdn.f(result);
			if (minPoint.length == 0 || value < minValue) {
				minValue = value;
				minPoint = result;
			}
			
			iter++;
			xn = model.getRandomPoint();
		}
		
		return new SPDNResult(SPDN.convertPoint(MatrixUtils.createRealVector(minPoint)).toArray(), spdn.f(minPoint), model.getAllParams(), ID, model.getId());
	}
}
