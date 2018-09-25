package algorithms.bsc.pso;

import algorithms.HyperParamsBuilder;

public class PSOHyperParamBuilder extends HyperParamsBuilder {
	protected int swarmSize = 20;
	protected int maxIter = 20;
	protected double tolerance = 0.001;
	protected double omega = 0.2;
	protected double fiParticle = 0.4;
	protected double fiGlobal = 0.8;
	
	@Override
	public PSOHyperParam build() {
		return new PSOHyperParam(swarmSize, maxIter, tolerance, omega, fiParticle, fiGlobal);
	}
	
	public PSOHyperParamBuilder swarmSize(int swarmSize) {
		this.swarmSize = swarmSize;
		return this;
	}

	public PSOHyperParamBuilder maxIter(int maxIter) {
		this.maxIter = maxIter;
		return this;
	}
	
	public PSOHyperParamBuilder tolerance(double tolerance) {
		this.tolerance = tolerance;
		return this;
	}
	
	public PSOHyperParamBuilder omega(double omega) {
		this.omega = omega;
		return this;
	}
	
	public PSOHyperParamBuilder fiParticle(double fiParticle) {
		this.fiParticle = fiParticle;
		return this;
	}
	
	public PSOHyperParamBuilder fiGlobal(double fiGlobal) {
		this.fiGlobal = fiGlobal;
		return this;
	}
}
