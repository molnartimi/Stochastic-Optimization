package algorithms.bsc.pso;

import java.util.SortedMap;
import java.util.TreeMap;

import algorithms.HyperParameters;

public class PSOHyperParam extends HyperParameters {

	public final int swarmSize, maxIter;
	public final double tolerance, omega, fiParticle, fiGlobal;
	
	public PSOHyperParam(int swarmSize, int maxIter, double tolerance, double omega, double fiParticle,
			double fiGlobal) {
		super();
		this.swarmSize = swarmSize;
		this.maxIter = maxIter;
		this.tolerance = tolerance;
		this.omega = omega;
		this.fiParticle = fiParticle;
		this.fiGlobal = fiGlobal;
	}

	@Override
	public SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("swarmSize", (double) swarmSize);
		map.put("maxIter", (double) maxIter);
		map.put("omega", omega);
		map.put("fiParticle", fiParticle);
		map.put("fiGlobal", fiGlobal);
		return map;
	}

}
