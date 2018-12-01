package algorithms.mo2tos.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algorithms.mo2tos.dto.Group;
import algorithms.mo2tos.exceptions.MeanNotCalculatedException;
import algorithms.mo2tos.exceptions.VarianceNotCalculatedException;

public class AllocationHandler {
	private int[] allocations;
	private final int ALLOC_NUM;
	
	public AllocationHandler(int groupNum, int heighModelSampleNumPerIter) {
		if (heighModelSampleNumPerIter / groupNum < 1) {
			throw new IllegalArgumentException("Number of heigh model sample calculation per iteration MUST be LARGER than number of groups.");
		}
		
		ALLOC_NUM = heighModelSampleNumPerIter;
		
		allocations = new int[groupNum];
		for (int i = 0; i < groupNum; i++) {
			allocations[i] = heighModelSampleNumPerIter / groupNum;
		}
	}
	
	public int getGroupAlloc(int i) {
		return allocations[i];
	}
	
	private void setGroupAlloc(int idx, double value) {
		allocations[idx] = (int) Math.round(value);
	}
	
	public void recalcAllocations(List<Group> groups) {
		if (groups.size() == 1) {
			allocations[0] = ALLOC_NUM;
		} else if (groups.size() == 0) {
			return;
		} else {
			allocations = new int[groups.size()];
			
			setBestToFirst(groups);
			List<Double> distancePerVarianceList = calcDistPerVar(groups);
			List<Double> distancePerVarianceRates = calcDistPerVarRates(distancePerVarianceList);
			List<Double> distancePerVarianceRatesProducts = calcDistPerVarRatesProducts(distancePerVarianceRates);
			
			double bestPart = getBestPart(groups, distancePerVarianceRatesProducts);
			double otherParts = getOtherParts(distancePerVarianceRatesProducts);
			double lastGroupAlloc = ALLOC_NUM / (bestPart + otherParts);
			
			setGroupAlloc(0, lastGroupAlloc * bestPart);
			setGroupAlloc(allocations.length - 1, lastGroupAlloc);
			for (int i = allocations.length - 2; i > 0; i--) {
				setGroupAlloc(i, allocations[i + 1] * distancePerVarianceRatesProducts.get(i - 1));
			}
		}
	}

	private void setBestToFirst(List<Group> groups) {
		Group best = getBestGroup(groups);
		groups.remove(best);
		groups.add(0, best);
	}
	
	private List<Double> calcDistPerVar(List<Group> groups) {
		List<Double> results = new ArrayList<>();
		Group best = groups.get(0);
		
		for (int i = 1; i < groups.size(); i++) {
			try {
				Group g = groups.get(i);
				double result = best.getDistanceFrom(g) / g.getVariance();
				results.add(result);
			} catch (MeanNotCalculatedException | VarianceNotCalculatedException e) {
				e.printStackTrace();
			}
		}
		
		return results;
	}
	
	private List<Double> calcDistPerVarRates(List<Double> distancePerVarianceList) {
		List<Double> results = new ArrayList<>();
		
		for (int i = 0; i < distancePerVarianceList.size() - 1; i++) {
			double rate = distancePerVarianceList.get(i + 1) / distancePerVarianceList.get(i);
			results.add(rate * rate);
		}
		
		return results;
	}
	

	private List<Double> calcDistPerVarRatesProducts(List<Double> distancePerVarianceRates) {
		List<Double> results = new ArrayList<>();

		results.add(1.0);
		for (int j = distancePerVarianceRates.size() - 1, i = 1; j >= 0; j--, i++) {
			double prevProduct = results.get(i - 1);
			double product = prevProduct * distancePerVarianceRates.get(j);
			results.add(product);
		}
		
		Collections.reverse(results);
		return results;
	}
	
	private double getBestPart(List<Group> groups, List<Double> distancePerVarianceRatesProducts) {
		double result = 0;
		try {
			for (int i = 0; i < distancePerVarianceRatesProducts.size(); i++) {
				double rate = distancePerVarianceRatesProducts.get(i) / groups.get(i + 1).getVariance();
				result += rate * rate;
			}

			result = Math.sqrt(result);
			result *= groups.get(0).getVariance();
		} catch (VarianceNotCalculatedException e) {
			e.printStackTrace();
		}
		return result;
	}
	

	private double getOtherParts(List<Double> distancePerVarianceRatesProducts) {
		double result = 0;
		for (double d: distancePerVarianceRatesProducts) {
			result += d;
		}
		return result;
	}
	
	private Group getBestGroup(List<Group> groups) {
		Group best = groups.get(0);
		for (Group g: groups) {
			try {
				if (g.getMean() < best.getMean()) {
					best = g;
				}
			} catch (MeanNotCalculatedException e) {
				e.printStackTrace();
			}
		}
		return best;
	}

}
