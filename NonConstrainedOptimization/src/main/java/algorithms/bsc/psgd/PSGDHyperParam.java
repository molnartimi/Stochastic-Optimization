package algorithms.bsc.psgd;

import java.util.SortedMap;

import algorithms.bsc.pso.PSOHyperParam;

public class PSGDHyperParam extends PSOHyperParam {
	public final int gradientMaxIter;
	public final double gamma;
	
	public PSGDHyperParam(int swarmSize, int maxIter, double tolerance, double omega, double fiParticle,
			double fiGlobal, int gradientMaxIter, double gamma) {
		super(swarmSize, maxIter, tolerance, omega, fiParticle, fiGlobal);
		this.gradientMaxIter = gradientMaxIter;
		this.gamma = gamma;
	}
	
	@Override
	public SortedMap<String, Double> getHyperParams() {
		SortedMap<String, Double> map = super.getHyperParams();
		map.put("gradientMaxIter", (double) gradientMaxIter);
		map.put("gamma", gamma);
		return map;
	}

}
