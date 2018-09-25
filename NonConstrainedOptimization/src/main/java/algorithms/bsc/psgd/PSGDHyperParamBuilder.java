package algorithms.bsc.psgd;

import algorithms.bsc.pso.PSOHyperParamBuilder;

public class PSGDHyperParamBuilder extends PSOHyperParamBuilder {
	private int gradientMaxIter = 5;
	private double gamma = 1;

	@Override
	public PSGDHyperParam build() {
		return new PSGDHyperParam(gradientMaxIter, gradientMaxIter, gamma, gamma, gamma, gamma, gradientMaxIter, gamma);
	}
	
	public PSGDHyperParamBuilder gradientMaxIter(int gradientMaxIter) {
		this.gradientMaxIter = gradientMaxIter;
		return this;
	}
	
	public PSGDHyperParamBuilder gamma(double gamma) {
		this.gamma = gamma;
		return this;
	}
	
	
	public PSGDHyperParamBuilder swarmSize(int swarmSize) {
		this.swarmSize = swarmSize;
		return this;
	}

	public PSGDHyperParamBuilder maxIter(int maxIter) {
		this.maxIter = maxIter;
		return this;
	}
	
	public PSGDHyperParamBuilder tolerance(double tolerance) {
		this.tolerance = tolerance;
		return this;
	}
	
	public PSGDHyperParamBuilder omega(double omega) {
		this.omega = omega;
		return this;
	}
	
	public PSGDHyperParamBuilder fiParticle(double fiParticle) {
		this.fiParticle = fiParticle;
		return this;
	}
	
	public PSGDHyperParamBuilder fiGlobal(double fiGlobal) {
		this.fiGlobal = fiGlobal;
		return this;
	}

}
