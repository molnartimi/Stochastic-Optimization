package algorithms;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;

public class GradientDescent {
	public static final String ID = "GRAD";
	
	private SPDN spdn;
	private Model model;
	
	public GradientDescent(Model model) {
		this.model = model;
		this.spdn = new SPDN(model);
	}

	public SPDNResult optimize(double gamma, double tolerance, double[] initPoint, int restart) {
		long startTime = System.nanoTime();
		
		RealVector minPoint = MatrixUtils.createRealVector(new double[model.getDim()]);
		minPoint.set(1000000.0);
		Double minValue = 1000000.0;
		int iter = 0;
		double initGamma = gamma;
		RealVector xn = initPoint.length > 0 ? MatrixUtils.createRealVector(initPoint) : MatrixUtils.createRealVector(model.getRandomPoint());
			
		while (iter < restart+1 && (minValue == null || minValue > tolerance)) {
			
			gamma = initGamma > 0 ? initGamma : 1.0;
			tolerance = tolerance > 0 ? tolerance : 0.001;
			
			try {
				RealVector xnBefore;
				RealVector xnNext = xn.add(spdn.df(xn).mapMultiply(-gamma));
				
				while (spdn.df(xnNext).getNorm() >= tolerance){
					xnBefore = xn.copy();
					xn = xnNext.copy();
						
					RealVector DxB = spdn.df(xnBefore);
					RealVector Dx = spdn.df(xn);
						
					gamma = xn.add(xnBefore.mapMultiply(-1)).dotProduct(Dx.add(DxB.mapMultiply(-1)));
					gamma /= Dx.add(DxB.mapMultiply(-1)).getNorm() * Dx.add(DxB.mapMultiply(-1)).getNorm();
						
					xnNext = xn.add(Dx.mapMultiply(-gamma));
				}
					
				double value = spdn.f(xnNext);
				if (minPoint == null || value < minValue) {
					minValue = value;
					minPoint = xnNext;
				}
			} catch (SpdnException e) {
				// Nothing to do, go and try again.
			}
			iter++;
			xn = MatrixUtils.createRealVector(model.getRandomPoint());
		}
		SPDNResult result = new SPDNResult(minValue, SPDN.convertPoint(minPoint).toArray(), ID, getHyperParams(initGamma, tolerance, restart), model);
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
	
	private SortedMap<String, Double> getHyperParams(double gamma, double tolerance, int restart) {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("gamma", gamma);
		map.put("tolerance", tolerance);
		map.put("restart", (double) restart);
		return map;
	}
	
	

}