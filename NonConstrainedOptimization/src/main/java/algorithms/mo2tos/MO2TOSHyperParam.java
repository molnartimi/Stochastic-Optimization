package algorithms.mo2tos;

import java.util.SortedMap;
import java.util.TreeMap;

import algorithms.HyperParameters;

public class MO2TOSHyperParam extends HyperParameters {
	public final double lowModelTol, heighModelTol;
	public final int lowModelSampleNum;
	public final int groupNum;
	public final int heighModelSampleNumPerIter;
	public final int maxIter;
	public final int maxError;
	
	public MO2TOSHyperParam(int lowCalcNum, double lowTol, double heighModelTol, int groupNum, int heighModelSampleNumPerIter, int maxIter, int maxError) {
		super();
		this.lowModelTol = lowTol;
		this.heighModelTol = heighModelTol;
		this.lowModelSampleNum = lowCalcNum;
		this.groupNum = groupNum;
		this.heighModelSampleNumPerIter = heighModelSampleNumPerIter;
		this.maxIter = maxIter;
		this.maxError = maxError;
	}

	@Override
	public SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("lowModelTol", lowModelTol);
		map.put("heighModelTol", heighModelTol);
		map.put("lowModelSampleNum", (double) lowModelSampleNum);
		map.put("groupNum", (double) groupNum);
		map.put("heighModelSampleNumPerIter", (double) heighModelSampleNumPerIter);
		map.put("maxIter", (double) maxIter);
		map.put("maxError", (double) maxError);
		return map;
	}

}
