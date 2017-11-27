package algorithms;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;

public class GradientDescent {
	public static final String ID = "GRAD";
	
	private SPDN spdn;
	private Model model;
	
	public GradientDescent(Model model) {
		this.model = model;
		this.spdn = new SPDN(model,0);
	}

	public SPDNResult optimize(double gamma, double tolerance, double[] initPoint, int restart) {		
		RealVector minPoint = null;
		Double minValue = null;
		int iter = 0;
		RealVector xn = initPoint.length > 0 ? MatrixUtils.createRealVector(initPoint) : MatrixUtils.createRealVector(model.getRandomPoint());
		
		while (iter < restart+1 && (minValue == null || minValue > tolerance)) {
			
			gamma = gamma > 0 ? gamma : 1.0;
			tolerance = tolerance > 0 ? tolerance : 0.001;
			
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
				
			iter++;
			xn = MatrixUtils.createRealVector(model.getRandomPoint());
		}
		
		return new SPDNResult(SPDN.convertPoint(minPoint).toArray(), spdn.f(minPoint), model.getAllParams(), ID, model.getId());
	}

}
