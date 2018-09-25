package algorithms.bsc.lbfgs;

import algorithms.HyperParamsBuilder;
import models.Model;

public class LBFGSHyperParamBuilder extends HyperParamsBuilder {
	private int m = 4;
	private int maxIter = 20;
	private int restart = 0;
	private double tolerance = 0.001;
	private double[] initPoint;
	
	public LBFGSHyperParamBuilder(Model model) {
		super();
		initPoint = model.getRandomPoint();
	}
	
	@Override
	public LBFGSHyperParam build() {
		return new LBFGSHyperParam(m, maxIter, tolerance, initPoint, restart);
	}
	
	public LBFGSHyperParamBuilder m(int m) {
		this.m = m;
		return this;
	}
	
	public LBFGSHyperParamBuilder maxIter(int maxIter) {
		this.maxIter = maxIter;
		return this;
	}
	
	public LBFGSHyperParamBuilder restart(int restart) {
		this.restart = restart;
		return this;
	}
	
	public LBFGSHyperParamBuilder tolerance(double tolerance) {
		this.tolerance = tolerance;
		return this;
	}
	
	public LBFGSHyperParamBuilder initPoint(double[] initPoint) {
		this.initPoint = initPoint;
		return this;
	}

}
