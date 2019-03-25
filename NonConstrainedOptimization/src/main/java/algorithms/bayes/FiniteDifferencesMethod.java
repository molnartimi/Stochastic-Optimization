package algorithms.bayes;

import java.util.function.Function;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

public class FiniteDifferencesMethod {
	/**
	 * Distance from x
	 */
	private double h = 1e-5;
	
	/**
	 * Function to approximate its derivation
	 */
	private Function<RealVector, Double> func;
	
	public FiniteDifferencesMethod(Function<RealVector, Double> function) {
		func = function;
	}
	public void setDistance(double h) {
		this.h = h;
	}
	
	public RealVector df(RealVector x) {
		double[] result = new double[x.getDimension()];
		for (int i = 0; i < x.getDimension(); i++) {
			double xi = x.getEntry(i);
			x.setEntry(i, xi + h);
			result[i] = func.apply(x);
			x.setEntry(i, xi - h);
			result[i] -= func.apply(x);
			x.setEntry(i, xi);
			result[i] /= 2 * h;
		}
		return MatrixUtils.createRealVector(result);
	}
}
