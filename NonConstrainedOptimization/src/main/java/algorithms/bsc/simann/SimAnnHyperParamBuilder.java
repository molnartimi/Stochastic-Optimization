package algorithms.bsc.simann;

import algorithms.HyperParamsBuilder;

public class SimAnnHyperParamBuilder extends HyperParamsBuilder {
	private double initTemp = 100;
	private double coolingRate = 0.9;
	private double initBorder = 0.3;
	private double borderSmallerRate = 0.95;
	private int restart = 0;
	private int innerRestart = 3;
	private double tolerance = 0.001;

	@Override
	public SimAnnHyperParam build() {
		return new SimAnnHyperParam(initTemp, coolingRate, initBorder, borderSmallerRate, tolerance, restart, innerRestart);
	}

	public SimAnnHyperParamBuilder initTemp(double initTemp) {
		this.initTemp = initTemp;
		return this;
	}

	public SimAnnHyperParamBuilder coolingRate(double coolingRate) {
		this.coolingRate = coolingRate;
		return this;
	}

	public SimAnnHyperParamBuilder initBorder(double initBorder) {
		this.initBorder = initBorder;
		return this;
	}

	public SimAnnHyperParamBuilder borderSmallerRate(double borderSmallerRate) {
		this.borderSmallerRate = borderSmallerRate;
		return this;
	}

	public SimAnnHyperParamBuilder tolerance(double tolerance) {
		this.tolerance = tolerance;
		return this;
	}
	
	public SimAnnHyperParamBuilder restart(int restart) {
		this.restart = restart;
		return this;
	}

	public SimAnnHyperParamBuilder innerRestart(int innerRestart) {
		this.innerRestart = innerRestart;
		return this;
	}

}
