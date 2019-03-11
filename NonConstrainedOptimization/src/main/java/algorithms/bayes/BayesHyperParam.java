package algorithms.bayes;

import java.util.SortedMap;
import java.util.TreeMap;

import algorithms.HyperParameters;

public class BayesHyperParam extends HyperParameters {
	public final int initSamples, maxSamples;
	public final double tolerance;

	public BayesHyperParam(int _initSamples, int _maxSamples, double _tolerance) {
		initSamples = _initSamples;
		maxSamples = _maxSamples;
		tolerance = _tolerance;
	}

	@Override
	public SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("initSamples", (double) initSamples);
		map.put("maxSamples", (double) maxSamples);
		map.put("tolerance", tolerance);
		return map;
	}

}
