package algorithms.bsc.gd;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import algorithms.Optimizer;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;

public class GradientDescent extends Optimizer<GDHyperParam> {
	public static final String ID = "GRAD";
	
	public GradientDescent(Model model) {
		super(model);
	}

	public SPDNResult optimize(GDHyperParam params) {
		long startTime = System.nanoTime();
		
		RealVector minPoint = MatrixUtils.createRealVector(new double[model.getDim()]);
		minPoint.set(1000000.0);
		Double minValue = 1000000.0;
		int iter = 0;
		RealVector xn = params.initPoint;
			
		while (iter < params.restart+1 && (minValue == null || minValue > params.tolerance)) {
			
			double gamma = params.gamma;
			
			try {
				RealVector xnBefore;
				RealVector xnNext = xn.add(spdn.df(xn).mapMultiply(-gamma));
				
				while (spdn.df(xnNext).getNorm() >= params.tolerance){
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
		SPDNResult result = new SPDNResult(minValue, SPDN.convertPoint(minPoint).toArray(), ID, params.getHyperParams(), model);
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
}
