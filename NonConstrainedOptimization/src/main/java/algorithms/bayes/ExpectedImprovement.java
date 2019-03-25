package algorithms.bayes;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.RealVector;

public class ExpectedImprovement {
	private GaussProcess gp;
	private NormalDistribution fi;
	private FiniteDifferencesMethod meanDiffMethod, varianceDiffMethod;
	
	private PartResults lastResult;

	public ExpectedImprovement(GaussProcess gp) {
		this.gp = gp;
		fi = new NormalDistribution();
		lastResult = new PartResults();
		meanDiffMethod = new FiniteDifferencesMethod(gp::getMean);
		varianceDiffMethod = new FiniteDifferencesMethod(gp::getVariance);
	}
	
	public double calc(RealVector p) {
		lastResult.point = p.copy();
		lastResult.mean = gp.getMean(p);
		lastResult.expectedDiff = gp.getBestPosition().value - lastResult.mean;
		lastResult.variance = gp.getVariance(p);
		lastResult.diffPerVariance = lastResult.expectedDiff / lastResult.variance;
		
		double expectedDiffPart = Math.max(lastResult.expectedDiff, 0);
		lastResult.densityPart = lastResult.variance * fi.density(lastResult.diffPerVariance);
		lastResult.distributionPart = Math.abs(lastResult.expectedDiff) * fi.cumulativeProbability(lastResult.diffPerVariance);
		
		return expectedDiffPart + lastResult.densityPart - lastResult.distributionPart;
	}
	
	public RealVector calcDx(RealVector p) {
		if (lastResult == null || lastResult.point == null ||  !lastResult.point.equals(p)) {
			calc(p);
		}
		if (lastResult.expectedDiff < 0) {
			RealVector meanD = meanDiffMethod.df(p);
			RealVector varianceD = varianceDiffMethod.df(p);
			RealVector meanPerVarD = meanD.mapMultiply(lastResult.variance)
					.subtract(varianceD.mapMultiply(lastResult.mean))
					.mapDivide(lastResult.variance * lastResult.variance);
			return meanD
					.add(varianceD.mapMultiply(fi.density(lastResult.diffPerVariance)))
					.add(meanPerVarD.mapMultiply(lastResult.variance * fiDensityDx(lastResult.diffPerVariance)))
					.subtract(meanD.mapMultiply(fi.cumulativeProbability(lastResult.diffPerVariance)))
					.subtract(meanPerVarD.mapMultiply(Math.abs(lastResult.expectedDiff) * fi.density(lastResult.diffPerVariance)));
		}
		return null;
	}
	
	private double fiDensityDx(double x) {
		return -1 / (Math.sqrt(2 * Math.PI)) * Math.exp(-(x * x) / 2) * Math.pow(x, 3);
	}

	private class PartResults {
		RealVector point;
		double expectedDiff;
		double mean;
		double variance;
		double diffPerVariance;
		double densityPart;
		double distributionPart;
	}
}
