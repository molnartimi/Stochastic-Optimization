package algorithms.bayes;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.RealVector;

public class ExpectedImprovement {
	private GaussProcess gp;
	private NormalDistribution fi;
	
	private PartResults lastResult;

	public ExpectedImprovement(GaussProcess gp) {
		this.gp = gp;
		fi = new NormalDistribution();
		lastResult = new PartResults();
	}
	
	public double calc(RealVector p) {
		lastResult.point = p.copy();
		lastResult.expectedDiff = gp.getMean(p) - gp.getBestPosition().value;
		lastResult.variance = gp.getVariance(p);
		lastResult.diffPerVariance = lastResult.expectedDiff / lastResult.variance;
		
		double expectedDiffPart = Math.min(lastResult.expectedDiff, 0);
		lastResult.densityPart = lastResult.variance * fi.density(lastResult.diffPerVariance);
		lastResult.distributionPart = Math.abs(lastResult.expectedDiff) * fi.cumulativeProbability(lastResult.diffPerVariance);
		
		return expectedDiffPart + lastResult.densityPart - lastResult.distributionPart;
	}
	
	public RealVector calcDx(RealVector p) {
		// TODO
		return null;
	}
	
	private double fiDensityDx(double x) {
		return -1 / (Math.sqrt(2 * Math.PI)) * Math.exp(-(x * x) / 2) * Math.pow(x, 3);
	}

	private class PartResults {
		RealVector point;
		double expectedDiff;
		double variance;
		double diffPerVariance;
		double densityPart;
		double distributionPart;
	}
}
