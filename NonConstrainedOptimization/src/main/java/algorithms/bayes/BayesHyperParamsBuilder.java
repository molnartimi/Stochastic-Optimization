package algorithms.bayes;

import java.util.ArrayList;
import java.util.List;

import algorithms.HyperParamsBuilder;

public class BayesHyperParamsBuilder extends HyperParamsBuilder {
	private int _initSamples = 20;
	private int _maxSamples = 100;
	private double _tolerance = 0.001;
	private double _alpha0 = 1; // TODO mennyi??
	private List<Double> _alpha; // TODO
	
	private int paramSize;
	
	public BayesHyperParamsBuilder(int paramSize) {
		this.paramSize = paramSize;
	}
	
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
	
	public BayesHyperParamsBuilder alpha(double alpha0, List<Double> alpha) {
		_alpha0 = alpha0;
		_alpha = alpha;
		return this;
	}

	@Override
	public BayesHyperParam build() {
		if (_alpha == null) {
			_alpha = new ArrayList<Double>();
			for (int i = 0; i < paramSize; i++) {
				_alpha.add(_alpha0);
			}
		}
		return new BayesHyperParam(_initSamples, _maxSamples, _alpha0, _alpha, _tolerance);
	}

}
