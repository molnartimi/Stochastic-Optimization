package algorithms.bsc.lbfgs;

import java.util.SortedMap;
import java.util.TreeMap;

import algorithms.HyperParameters;

public class LBFGSHyperParam extends HyperParameters {
	public final int m, maxIter, restart;
	public final double tolerance;
	public final double[] initPoint;
	
	public LBFGSHyperParam(int m, int maxIter, double tolerance, double[] initPoint, int restart) {
		super();
		this.m = m;
		this.maxIter = maxIter;
		this.tolerance = tolerance;
		this.initPoint = initPoint;
		this.restart = restart;
	}
	
	@Override
	public SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("m", (double) m);
		map.put("maxIter", (double) maxIter);
		map.put("tolerance", tolerance);
		map.put("restart", (double) restart);
		return map;
	}

}
