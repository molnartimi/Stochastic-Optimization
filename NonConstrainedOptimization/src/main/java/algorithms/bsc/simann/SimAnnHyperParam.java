package algorithms.bsc.simann;

import java.util.SortedMap;
import java.util.TreeMap;

import algorithms.HyperParameters;

public class SimAnnHyperParam extends HyperParameters {
	public final double  initTemp, coolingRate, initBorder, borderSmallerRate, tolerance;
	public final int restart, innerRestart;

	public SimAnnHyperParam(double initTemp, double coolingRate, double initBorder, double borderSmallerRate,
			double tolerance, int restart, int innerRestart) {
		super();
		this.initTemp = initTemp;
		this.coolingRate = coolingRate;
		this.initBorder = initBorder;
		this.borderSmallerRate = borderSmallerRate;
		this.tolerance = tolerance;
		this.restart = restart;
		this.innerRestart = innerRestart;
	}

	@Override
	public SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("initTemp", initTemp);
		map.put("coolingRate", coolingRate);
		map.put("border", initBorder);
		map.put("borderSmallerRate", borderSmallerRate);
		return map;
	}

}
