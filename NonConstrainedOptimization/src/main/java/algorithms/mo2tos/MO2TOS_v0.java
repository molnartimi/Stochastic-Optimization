package algorithms.mo2tos;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import algorithms.mo2tos.dto.Group;
import algorithms.mo2tos.dto.Sample;
import algorithms.mo2tos.exceptions.EmptyGroupException;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import model.Model;

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
				List<Double> point = model.randomParamValues();
				double result = modelChecker.calcObjective(point);
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
		while (iter > 0 && groups.size() > 1) {
			List<Group> groupsToDelete = new ArrayList<>();
			calcRandomSamples(groups, maxError);
			for (int i = 0; i < groups.size(); i++) {
				try {
					groups.get(i).calcMeanAndVariance(maxError);
				} catch (SpdnException e) {
					groupsToDelete.add(groups.get(i));
				}
			}
			if (groupsToDelete.size() > 0) {
				deleteIncalculableGroups(groups, groupsToDelete);
			}
			allocationHandler.recalcAllocations(groups);
			iter--;
		}
		return getGlobalBest(groups);
	}

	private void calcRandomSamples(List<Group> groups, int maxError) {
		List<Group> groupsToDelete = new ArrayList<>();
		for (int i = 0; i < groups.size(); i++) {
			for (int j = 0; j < allocationHandler.getGroupAlloc(i); j++) {
				try {
					groups.get(i).calcNextRandomSample(modelChecker, maxError);
				} catch (EmptyGroupException e) {
					groupsToDelete.add(groups.get(i));
					break;
				}
			}
		}
		if (groupsToDelete.size() > 0) {
			deleteIncalculableGroups(groups, groupsToDelete);
		}
	}

	private void deleteIncalculableGroups(List<Group> groups, List<Group> groupsToDelete) {
		for (Group toDelete: groupsToDelete) {
			groups.remove(toDelete);
		}
		logger.info(groupsToDelete.size() + " groups removed, remain " + groups.size());
	}

	private Sample getGlobalBest(List<Group> groups) {
		Sample globalBestSample = groups.get(0).getLocalBest();
		for (int i = 1; i < groups.size(); i++) {
			Sample localBestSample = groups.get(i).getLocalBest();
			if (localBestSample.getHeighResult() < globalBestSample.getHeighResult()) {
				globalBestSample = localBestSample;
			}
		}
		return globalBestSample;
	}
}
