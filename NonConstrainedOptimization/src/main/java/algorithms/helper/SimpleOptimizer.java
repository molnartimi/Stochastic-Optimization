package algorithms.helper;

import java.util.function.Function;

import org.apache.commons.math3.linear.RealVector;

import algorithms.HyperParameters;
import algorithms.Sample;
import model.Model;

public abstract class SimpleOptimizer<P extends HyperParameters> {
	protected Model model;
	
	public SimpleOptimizer(Model model) {
		this.model = model;
	}
	
	public abstract Sample<RealVector> optimize(Function<RealVector, Double> f, Function<RealVector, RealVector> df, P params);
}
