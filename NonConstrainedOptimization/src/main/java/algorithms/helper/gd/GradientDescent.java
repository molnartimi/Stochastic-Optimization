package algorithms.helper.gd;

import java.util.function.Function;

import org.apache.commons.math3.linear.RealVector;

import algorithms.Sample;
import algorithms.helper.SimpleOptimizer;
import model.Model;

public class GradientDescent extends SimpleOptimizer<GDHyperParam> {

	public GradientDescent(Model model) {
		super(model);
	}

	@Override
	public Sample<RealVector> optimize(Function<RealVector, Double> f, Function<RealVector, RealVector> df, GDHyperParam params) {
		DerivativeSample bestPoint = new DerivativeSample(params.initPoint, f.apply(params.initPoint), df.apply(params.initPoint));
		DerivativeSample act = bestPoint;

		for (int i = 0; i < params.restart; i++) {
				double gamma = params.gamma;
				
				while (act.derivative.getNorm() >= params.tolerance) {
					DerivativeSample before = act;
					
					RealVector newPoint = act.point.add(act.derivative).mapMultiply(-gamma);
					act = new DerivativeSample(newPoint, f.apply(newPoint), df.apply(newPoint));
					
					if (act.value < bestPoint.value) {
						bestPoint = act;
					}
					
					gamma = act.point.add(before.point.mapMultiply(-1)).dotProduct(act.derivative.add(before.derivative.mapMultiply(-1)));
					gamma /= act.derivative.add(before.derivative.mapMultiply(-1)).getNorm() * act.derivative.add(before.derivative.mapMultiply(-1)).getNorm();
					
				}
				RealVector nextInitPoint = model.randomVector();
				act = new DerivativeSample(nextInitPoint, f.apply(nextInitPoint), df.apply(nextInitPoint));
		}
		return bestPoint;
	}
		
	private class DerivativeSample extends Sample<RealVector> {
		public final RealVector derivative;
		
		public DerivativeSample(RealVector point, double value, RealVector derivative) {
			super(point, value);
			this.derivative = derivative;
		}	
	}

}
