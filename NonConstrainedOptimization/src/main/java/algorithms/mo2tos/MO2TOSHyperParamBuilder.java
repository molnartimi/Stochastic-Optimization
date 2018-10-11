package algorithms.mo2tos;

import algorithms.HyperParamsBuilder;

public class MO2TOSHyperParamBuilder extends HyperParamsBuilder {
	private double lowModelTol = 1e-2;
	private double heighModelTol = 1e-9;
	private int lowModelSamplesNum = 100000;
	private int groupNum = 100;
	private int heighModelSampleNumPerIter = 100;
	private int maxIter = 20;
	private int maxError = 20;
	
	@Override
	public MO2TOSHyperParam build() {
		return new MO2TOSHyperParam(lowModelSamplesNum, lowModelTol, heighModelTol, groupNum, heighModelSampleNumPerIter, maxIter, maxError);
	}
	
	public MO2TOSHyperParamBuilder lowModelTolerance(double lowModelTol) {
		this.lowModelTol = lowModelTol;
		return this;
	}
	
	public MO2TOSHyperParamBuilder heighModelTolerance(double heighModelTol) {
		this.heighModelTol = heighModelTol;
		return this;
	}
	
	public MO2TOSHyperParamBuilder groupNumber(int k) {
		this.groupNum = k;
		return this;
	}

	public MO2TOSHyperParamBuilder heighModelSampleNumPerIter(int n) {
		this.heighModelSampleNumPerIter = n;
		return this;
	}
	
	public MO2TOSHyperParamBuilder lowModelSampleNum(int n) {
		this.lowModelSamplesNum = n;
		return this;
	}
	
	public MO2TOSHyperParamBuilder maxIter(int maxIter) {
		this.maxIter = maxIter;
		return this;
	}
	
	public MO2TOSHyperParamBuilder maxError(int maxError) {
		this.maxError = maxError;
		return this;
	}
}
