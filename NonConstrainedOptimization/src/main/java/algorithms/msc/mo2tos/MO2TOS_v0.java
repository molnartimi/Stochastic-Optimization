package algorithms.msc.mo2tos;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import algorithms.msc.mo2tos.exceptions.EmptyGroupException;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;

public class MO2TOS_v0 extends MO2TOS {

	public MO2TOS_v0(Model model) {
		super(model);
	}
	
	@Override
	protected SortedSet<Sample> ordinalTransform(int lowModelSampleNum, int maxError) {
		SortedSet<Sample> ordinalSpace = new TreeSet<>();
		int errorsInARow = 0;
		
		while (lowModelSampleNum > 0) {
			try {
				double[] point = model.getRandomPoint();
				double result = spdn.f(point);
				ordinalSpace.add(new Sample(point, result));
				
				lowModelSampleNum--;
				errorsInARow = 0;
			} catch (SpdnException e) {
				if (++errorsInARow >= maxError) {
					throw new SpdnException("Spdn failed " + errorsInARow + " times in a row, stopped trying.");
				}
			}
		}
		
		return ordinalSpace;
	}

	@Override
	protected Sample optimalSample(int iter, List<Group> groups, int maxError) {
		while (iter > 0) {
			calcRandomSamples(groups, maxError);
			for (int i = 0; i < groups.size(); i++) {
				try {
					groups.get(i).calcMeanAndVariance(spdn, maxError);
				} catch (SpdnException e) {
					System.out.println("Group removed");
					groups.remove(i);
				}
			}
			allocationHandler.recalcAllocations(groups);
			iter--;
		}
		return getGlobalBest(groups);
	}

	private void calcRandomSamples(List<Group> groups, int maxError) {
		for (int i = 0; i < groups.size(); i++) {
			for (int j = 0; j < allocationHandler.get(i); j++) {
				try {
					groups.get(i).calcNextRandomSample(spdn, maxError);
				} catch (EmptyGroupException e) {
					System.out.println("Group removed");
					groups.remove(i);
				}
			}
		}
	}

	private Sample getGlobalBest(List<Group> groups) {
		Sample globalBestSample = groups.get(0).getLocalBest();
		for (int i = 1; i < groups.size(); i++) {
			Sample localBestSample = groups.get(i).getLocalBest();
			if (localBestSample.getHeighResult() > globalBestSample.getHeighResult()) {
				globalBestSample = localBestSample;
			}
		}
		return null;
	}
}
