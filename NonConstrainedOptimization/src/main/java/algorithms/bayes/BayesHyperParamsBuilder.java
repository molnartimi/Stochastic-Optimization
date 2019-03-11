package algorithms.bayes;

import algorithms.HyperParamsBuilder;

public class BayesHyperParamsBuilder extends HyperParamsBuilder {
	private int _initSamples = 20;
	private int _maxSamples = 100;
	private double _tolerance = 0.001;
	
	public BayesHyperParamsBuilder initSamples(int n) {
		_initSamples = n;
		return this;
	}
	
	public BayesHyperParamsBuilder maxSamples(int n) {
		_maxSamples = n;
		return this;
	}
	
	public BayesHyperParamsBuilder tolerance(double tol) {
		_tolerance = tol;
		return this;
	}

	@Override
	public BayesHyperParam build() {
		return new BayesHyperParam(_initSamples, _maxSamples, _tolerance);
	}

}
