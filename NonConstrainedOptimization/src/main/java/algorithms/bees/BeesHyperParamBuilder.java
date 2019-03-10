package algorithms.bees;

import algorithms.HyperParamsBuilder;

public class BeesHyperParamBuilder extends HyperParamsBuilder {

	private int _scoutSize = 20;
	private int _bestBeesSize = 8;
	private int _eliteBeesSize = 3;
	private int _recruitedOfBestsSize = 5;
	private int _recruitedOfElitesSize = 10;
	private double _tolerance = 0.001;
	
	private int _maxIter = 20;
	private double _radius = 0.5;
	private double _radiusSmallerRate = 0.9;
	
	public BeesHyperParamBuilder scoutSize(int scoutSize) {
		_scoutSize = scoutSize;
		return this;
	}

	public BeesHyperParamBuilder bestBeesSize(int bestBeesSize) {
		_bestBeesSize = bestBeesSize;
		return this;
	}

	public BeesHyperParamBuilder eliteBeesSize(int eliteBeesSize) {
		_eliteBeesSize = eliteBeesSize;
		return this;
	}

	public BeesHyperParamBuilder recruitedOfBestsSize(int recruitedOfBestsSize) {
		_recruitedOfBestsSize = recruitedOfBestsSize;
		return this;
	}

	public BeesHyperParamBuilder recruitedOfElitesSize(int recruitedOfElitesSize) {
		_recruitedOfElitesSize = recruitedOfElitesSize;
		return this;
	}

	public BeesHyperParamBuilder maxIter(int maxIter) {
		_maxIter = maxIter;
		return this;
	}

	public BeesHyperParamBuilder radius(double radius) {
		_radius = radius;
		return this;
	}

	public BeesHyperParamBuilder radiusSmallerRate(double radiusSmallerRate) {
		_radiusSmallerRate = radiusSmallerRate;
		return this;
	}
	
	public BeesHyperParamBuilder tolerance(double tolerance) {
		_tolerance = tolerance;
		return this;
	}
	
	@Override
	public BeesHyperParam build() {
		return new BeesHyperParam(_scoutSize, _bestBeesSize, _eliteBeesSize,
				_recruitedOfBestsSize, _recruitedOfElitesSize,
				_maxIter,
				_radius, _radiusSmallerRate, _tolerance);
	}
	
	

}
