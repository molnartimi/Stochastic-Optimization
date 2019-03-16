package algorithms.bayes;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import algorithms.HyperParameters;

public class BayesHyperParam extends HyperParameters {
	public final int initSamples, maxSamples;
	public final double alpha0;
	public final List<Double> alpha;
	public final double tolerance;

	public BayesHyperParam(int _initSamples, int _maxSamples, double _alpha0, List<Double> _alpha, double _tolerance) {
		initSamples = _initSamples;
		maxSamples = _maxSamples;
		alpha0 = _alpha0;
		alpha = _alpha;
		tolerance = _tolerance;
	}

	@Override
	public SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("initSamples", (double) initSamples);
		map.put("maxSamples", (double) maxSamples);
		map.put("alpha0", alpha0);
		// TODO alpha vektor?
		map.put("tolerance", tolerance);
		return map;
	}

}
