package algorithms.helper.gd;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.linear.RealVector;

import algorithms.HyperParameters;

public class GDHyperParam extends HyperParameters {
	public final double gamma, tolerance;
	public final RealVector initPoint;
	public final int restart, gradSearchMaxIter;
	
	public GDHyperParam(double gamma, double tolerance, RealVector initPoint, int restart, int _gradSearchMaxIter) {
		super();
		this.gamma = gamma;
		this.tolerance = tolerance;
		this.initPoint = initPoint;
		this.restart = restart;
		this.gradSearchMaxIter = _gradSearchMaxIter;
	}
	
	@Override
	public SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("gamma", gamma);
		map.put("tolerance", tolerance);
		map.put("restart", (double) restart);
		map.put("gradSearchMaxIter", (double) gradSearchMaxIter);
		return map;
	}
}
