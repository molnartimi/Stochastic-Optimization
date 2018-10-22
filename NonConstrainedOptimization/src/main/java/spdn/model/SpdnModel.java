package spdn.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;
import umontreal.ssj.hups.LatinHypercube;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.rng.RandomStreamBase;
import umontreal.ssj.rng.RandomStreamFactory;

public class SpdnModel {
	
	public final String filePath;
	public final String name;
	public final String id;
	public final transient List<Parameter> simpleParameterList;
	public final transient List<Reward> simpleRewardList;
	public final transient List<SpdnParameter> SpdnParameterList;
	public final transient List<SpdnReward> SpdnRewardList;
	
	public SpdnModel(String filePath, String name, String id, List<SpdnParameter> parameterList, List<SpdnReward> rewardList) {
		this.filePath = filePath;
		this.name = name;
		this.id = id;
		this.SpdnParameterList = Collections.unmodifiableList(parameterList);
		this.SpdnRewardList = Collections.unmodifiableList(rewardList);
		
		this.simpleParameterList = Collections.unmodifiableList(
				SpdnParameterList.stream()
									.map(handler -> handler.parameter)
									.collect(Collectors.toList()));
		
		this.simpleRewardList = Collections.unmodifiableList(
				SpdnRewardList.stream()
									.map(handler -> handler.reward)
									.collect(Collectors.toList()));
	}
	
	public int rewardSize() {
		return SpdnRewardList.size();
	}
	
	public int parameterSize() {
		return SpdnParameterList.size();
	}
	
	public double getSquareErrorOfReward(int idx, double result) {
		return SpdnRewardList.get(idx).getSqareError(result);
	}

	public List<Double> getDefaultValues() {
		return SpdnParameterList.stream().map(param -> param.defaultValue).collect(Collectors.toList());
	}

	public List<Double> randomParamValues() {
		List<Double> point = new ArrayList<>();
		Random rand = new Random();
		
		for(SpdnParameter param: SpdnParameterList) {
			point.add(rand.nextDouble() * (param.maxValue - param.minValue) + param.minValue);
		}
		
		return point;
	}
	
	public List<List<Double>> latinHypercubeParamValues(int n) {
		LatinHypercube cube = new LatinHypercube(n, parameterSize());
		cube.randomize(new MRG32k3a());
		return convertLatinHypercubeResult(cube.getArray());
	}

	/**
	 * Convert double[][] array which returned by LatinHypercube into List<List<Double>>, with valid parameter values (not between 0 and 1)
	 * 
	 * @param valuesArray
	 * @return
	 */
	private List<List<Double>> convertLatinHypercubeResult(double[][] valuesArray) {
		List<List<Double>> points = new ArrayList<>();
		for (int i = 0; i < valuesArray.length; i++) {
			List<Double> values = new ArrayList<>();
			for (int j = 0; j < parameterSize(); j++) {
				values.add(getRandomParamValueFromNormal(j, valuesArray[i][j]));
			}
			points.add(values);
		}
		return points;
	}

	/**
	 * Calculate the random param value form normalized value to value between parameter borders.
	 * 
	 * @param paramIdx - index of parameter in list of parameters
	 * @param normalValue - 0 <= normalValue < 1
	 * @return
	 */
	private Double getRandomParamValueFromNormal(int paramIdx, double normalValue) {
		SpdnParameter param = SpdnParameterList.get(paramIdx);
		return (normalValue * (param.maxValue - param.minValue) + param.minValue);
	}
}
