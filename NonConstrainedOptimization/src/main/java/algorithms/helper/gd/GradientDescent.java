package algorithms.helper.gd;

import java.util.function.Function;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;

import algorithms.Sample;
import model.Model;

public class GradientDescent {
	protected Model model;
	protected GaussianRandomGenerator randomGenerator = new GaussianRandomGenerator(new JDKRandomGenerator());

	public GradientDescent(Model model) {
		this.model = model;
	}

	/*
	 * TODO stop algorithm on border instead of model.cutParamsOnBorder!
	 */
	public Sample<RealVector> optimize(Function<RealVector, Double> f, Function<RealVector, RealVector> df, GDHyperParam params) throws GradientDescentException {
		DerivativeSample bestPoint = new DerivativeSample(params.initPoint.copy(), f.apply(params.initPoint), df.apply(params.initPoint));
		
		try {
			bestPoint = getValidInitPoint(bestPoint, f, df);
		} catch (GradientDescentException e) {
			throw new GradientDescentException("Gradient descent could not find differentiable init point.");
		}

		DerivativeSample act = bestPoint;

		for (int i = 0; i <= params.restart; i++) {
				double gamma = params.gamma;
				
				while (act.derivative.getNorm() >= params.tolerance) {
					DerivativeSample before = act;
					
					RealVector newPoint = act.point.add(act.derivative).mapMultiply(-gamma);
					model.cutParamsOnBorder(newPoint);
					act = new DerivativeSample(newPoint, f.apply(newPoint), df.apply(newPoint));
					try {
						act = fixNullableDerivative(f, df, act);
					} catch (GradientDescentException e) {
						break;
					}
					
					if (act.value < bestPoint.value) {
						bestPoint = act;
					}
					
					gamma = act.point.add(before.point.mapMultiply(-1)).dotProduct(act.derivative.add(before.derivative.mapMultiply(-1)));
					gamma /= act.derivative.add(before.derivative.mapMultiply(-1)).getNorm() * act.derivative.add(before.derivative.mapMultiply(-1)).getNorm();
					
				}
				RealVector nextInitPoint = model.randomVector();
				act = new DerivativeSample(nextInitPoint, f.apply(nextInitPoint), df.apply(nextInitPoint));
				try {
					act = getValidInitPoint(act, f, df);
				} catch (GradientDescentException e) {
					return bestPoint;
				}
		}
		return bestPoint;
	}

	private DerivativeSample getValidInitPoint(DerivativeSample point, Function<RealVector, Double> f, Function<RealVector, RealVector> df) throws GradientDescentException {
		try {
			point = fixNullableDerivative(f, df, point);
		} catch (GradientDescentException e) {
			int iter = 0;
			int maxIter = 10;
			do {
				RealVector newRandomInit = model.randomVector();
				point = new DerivativeSample(newRandomInit, f.apply(newRandomInit), df.apply(newRandomInit));
				iter++;
			} while (point.derivative == null && iter < maxIter);
			if (iter == maxIter) {
				throw new GradientDescentException();
			}
		}
		return point;
	}
	
	private DerivativeSample fixNullableDerivative(Function<RealVector, Double> f, Function<RealVector, RealVector> df,
			DerivativeSample point) throws GradientDescentException {
		int iter = 0;
		int maxIter = 10;
		while (point.derivative == null && iter <= maxIter) {
			for (int i = 0; i < model.parameterSize(); i++)
				point.point.addToEntry(i, randomGenerator.nextNormalizedDouble());
			model.cutParamsOnBorder(point.point);
			point = new DerivativeSample(point.point, f.apply(point.point), df.apply(point.point));
			iter++;
		}
		if (iter > maxIter) {
			throw new GradientDescentException();
		}
		return point;
	}
		
	private class DerivativeSample extends Sample<RealVector> {
		public final RealVector derivative;
		
		public DerivativeSample(RealVector point, double value, RealVector derivative) {
			super(point, value);
			this.derivative = derivative;
		}	
	}

}
