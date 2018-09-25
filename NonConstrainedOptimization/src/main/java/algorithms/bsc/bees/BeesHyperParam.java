package algorithms.bsc.bees;

import java.util.SortedMap;
import java.util.TreeMap;

import algorithms.HyperParameters;

public class BeesHyperParam extends HyperParameters {
	final public int scoutSize, bestBeesSize, eliteBeesSize;
	final public int recruitedOfBestsSize, recruitedOfElitesSize;
	final public int maxIter;
	final public double radius, radiusSmallerRate;
	
	public BeesHyperParam(int scoutSize, int bestBeesSize, int eliteBeesSize,
			int recruitedOfBestsSize, int recruitedOfElitesSize,
			int maxIter,
			double radius, double radiusSmallerRate) {
		super();
		this.scoutSize = scoutSize;
		this.bestBeesSize = bestBeesSize;
		this.eliteBeesSize = eliteBeesSize;
		this.recruitedOfBestsSize = recruitedOfBestsSize;
		this.recruitedOfElitesSize = recruitedOfElitesSize;
		this.maxIter = maxIter;
		this.radius = radius;
		this.radiusSmallerRate = radiusSmallerRate;
	}
	
	@Override
	public SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("maxIter", (double) this.maxIter);
		map.put("initRadius", this.radius);
		map.put("radiusSmallerRate", this.radiusSmallerRate);
		map.put("scoutSize", (double) this.scoutSize);
		map.put("bestBeesSize", (double) this.bestBeesSize);
		map.put("eliteBeesSize", (double) this.eliteBeesSize);
		map.put("recruitedOfBestsSize", (double) this.recruitedOfBestsSize);
		map.put("recruitedOfElitesSize", (double) this.recruitedOfElitesSize);
		return map;
	}
}
