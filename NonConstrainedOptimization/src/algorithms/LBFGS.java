package algorithms;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;

import hu.bme.mit.inf.optimization.wrapper.breeze.LbfgsWrapper;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;


public class LBFGS {
	public static final String ID = "BFGS";
	
	private SPDN spdn;
	private Model model;
	
	public LBFGS(Model model) {
		this.model = model;
		this.spdn = new SPDN(model);
	}

	public SPDNResult optimize(int m, int maxIter, double tolerance, double[] initPoint, int restart) {
		long startTime = System.nanoTime();

		m = m > 0 ? m : 4; 
		maxIter = maxIter > 0 ? maxIter : 20;
		tolerance = tolerance > 0 ? tolerance : 0.001;
		
		double[] minPoint = new double[model.getDim()];
		double minValue = 10000000;
		int iter = 0;
		double[] xn = initPoint.length > 0 ? initPoint : model.getRandomPoint();
		
		LbfgsWrapper lbfgs = new LbfgsWrapper(maxIter,m,tolerance);
		while (iter < restart+1 && minValue > tolerance) {
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
											getHyperParams(m, maxIter, tolerance, restart), 
											model);
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
	
	private SortedMap<String, Double> getHyperParams(int m, int maxIter, double tolerance, int restart) {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("m", (double) m);
		map.put("maxIter", (double) maxIter);
		map.put("tolerance", tolerance);
		map.put("restart", (double) restart);
		return map;
	}
}
