package algorithms.helper.gd;

import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.math3.linear.RealVector;

import algorithms.Sample;

public class GradientDescent {

	protected Function<RealVector, Double> f;
	protected Function<RealVector, RealVector> df;
	protected Supplier<RealVector> getRandomVector;
	protected Function<RealVector, Boolean> isOutOfBound;

	public GradientDescent(Function<RealVector, Double> f, Function<RealVector, RealVector> df, Supplier<RealVector> getRandomVector, Function<RealVector, Boolean> isOutOfBound) {
		this.f = f;
		this.df = df;
		this.getRandomVector = getRandomVector;
		this.isOutOfBound = isOutOfBound;
	}

	/*
	 * TODO stop algorithm on border instead of model.cutParamsOnBorder!
	 */
	public Sample<RealVector> optimize(GDHyperParam params) throws GradientDescentException {
		
		DerivativeSample initPoint;
		try {
			initPoint = getDiffableInitSample(params.initPoint, params.gradSearchMaxIter);
		} catch (GradientDescentException e) {
			throw new GradientDescentException("Gradient descent could not find differentiable init point. (Tried " + params.gradSearchMaxIter + " times)");
		}
		DerivativeSample bestPoint = initPoint;
		DerivativeSample act = initPoint;

		for (int i = 0; i <= params.restart; i++) {
				double gamma = params.gamma;
				
				while (act.derivative.getNorm() >= params.tolerance) {
					DerivativeSample before = act;
					
					RealVector newPoint = act.point.add(act.derivative).mapMultiply(-gamma);
					if (isOutOfBound.apply(newPoint)) 
						break;
					
					act = new DerivativeSample(newPoint, f.apply(newPoint), df.apply(newPoint));
					if (act.derivative == null) 
						break;
					
					if (act.value < bestPoint.value) {
						bestPoint = act;
					}
					
					gamma = act.point.add(before.point.mapMultiply(-1)).dotProduct(act.derivative.add(before.derivative.mapMultiply(-1)));
					gamma /= act.derivative.add(before.derivative.mapMultiply(-1)).getNorm() * act.derivative.add(before.derivative.mapMultiply(-1)).getNorm();
					
				}
				RealVector nextInitPoint = getRandomVector.get();
				try {
					act = getDiffableInitSample(nextInitPoint, params.gradSearchMaxIter);
				} catch (GradientDescentException e) {
					return bestPoint;
				}
		}
		return bestPoint;
	}

	private DerivativeSample getDiffableInitSample(RealVector initPoint, int maxIter) throws GradientDescentException {
		RealVector initP = initPoint;
		RealVector grad = df.apply(initP);
		int iter = 0;
		while (grad == null && iter < maxIter) {
			initP = getRandomVector.get();
			grad = df.apply(initP);
			iter++;
		}
		if (grad == null && iter == maxIter) {
			throw new GradientDescentException();
		} else {
			return new DerivativeSample(initP, f.apply(initP), grad);
		}
	}
		
	private class DerivativeSample extends Sample<RealVector> {
		public final RealVector derivative;
		
		public DerivativeSample(RealVector point, double value, RealVector derivative) {
			super(point, value);
			this.derivative = derivative;
		}	
	}

}
