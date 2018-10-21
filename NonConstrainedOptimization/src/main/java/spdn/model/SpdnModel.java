package spdn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;

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
}
