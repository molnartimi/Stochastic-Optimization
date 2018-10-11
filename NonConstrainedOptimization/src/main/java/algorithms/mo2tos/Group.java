package algorithms.mo2tos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algorithms.mo2tos.exceptions.EmptyGroupException;
import algorithms.mo2tos.exceptions.MeanNotCalculatedException;
import algorithms.mo2tos.exceptions.VarianceNotCalculatedException;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import spdn.SPDN;

public class Group {
	private List<Sample> samples, countedSamples;
	private Double mean;
	private Double variance;
	private Random rand;
	
	private interface IteratorOverSamples {
		double changeValue(double actValue, double result);
	}

	public Group(List<Sample> samples) {
		this.samples = samples;
		this.countedSamples = new ArrayList<>();
		rand = new Random();
	}
	

	public Sample getLocalBest() {
		Sample localBestSample = countedSamples.get(0);
		for (int i = 1; i < countedSamples.size(); i++) {
			if (localBestSample.getHeighResult() > countedSamples.get(i).getHeighResult()) {
				localBestSample = countedSamples.get(i);
			}
		}
		return localBestSample;
	}
	
	public void calcNextRandomSample(SPDN spdn, int maxError) throws EmptyGroupException {
		calcNextRandomSample(spdn, 0, maxError);
	}
	
	public double getDistanceFrom(Group g) throws MeanNotCalculatedException {
		return this.getMean() - g.getMean();
	}
	
	public double getMean() throws MeanNotCalculatedException {
		if (mean == null) {
			throw new MeanNotCalculatedException("You have to call calcMeanAndVariance method before get Mean.");
		} 
		return mean;
	}
	
	public double getVariance() throws VarianceNotCalculatedException {
		if (variance == null) {
			throw new VarianceNotCalculatedException("You have to call calcMeanAndVariance method before get Variance.");
		}
		return variance;
	}
	
	public void calcMeanAndVariance(SPDN spdn, int maxError) throws SpdnException{
		double sum1 = calcWithSamples(spdn, 0, (actValue, result) -> {return actValue + result;}, maxError);
		mean = sum1 / countedSamples.size();
		
		double sum2 = calcWithSamples(spdn, 0, (actValue, result) -> {return actValue + Math.pow(result - mean, 2);}, maxError);
		variance = Math.sqrt(sum2 / countedSamples.size());
	}
	
	private double calcWithSamples(SPDN spdn, double actValue, IteratorOverSamples iterator, int maxError) {
		int errorsInARow = 0;
		for (int i = 0; i < countedSamples.size(); i++) {
			try {
				Sample s = countedSamples.get(i);
				Double x = s.isComputed() ? s.getHeighResult() : spdn.f(s.values);
				actValue = iterator.changeValue(actValue, x);
				
				errorsInARow = 0;
			} catch(SpdnException e) {
				countedSamples.remove(i);
				i--;
				if (++errorsInARow >= maxError) {
					throw new SpdnException("Spdn failed " + errorsInARow + " times in a row at calculate mean and/or variance of group.");
				}
			}
		}
		return actValue;
	}
	
	private void calcNextRandomSample(SPDN spdn, int raisedErrors, int maxError) throws EmptyGroupException {
		Sample chosen = getNextRandom();
		try {
			chosen.setHeighResult(spdn.f(chosen.values));
		} catch (SpdnException e) {
			if (++raisedErrors >= maxError) {
				throw new SpdnException("Spdn failed " + raisedErrors + " times in a row at calculating samples with heigh fidelity model in a group.");
			} else {
				calcNextRandomSample(spdn, raisedErrors, maxError);
			}
		}
	}
	
	private Sample getNextRandom() throws EmptyGroupException{
		if (samples.size() == 0) {
			throw new EmptyGroupException();
		}
		Sample chosen = samples.remove(rand.nextInt(samples.size()));
		countedSamples.add(chosen);
		return chosen;
	}
}
