package spdn;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;

public class SPDNModel {
	
	public final String filePath;
	public final String name;
	public final String id;
	public final List<Parameter> parameterList;
	public final List<Reward> rewardList;
	public final List<ParameterHandler> parameterHandlerList;
	public final List<RewardHandler> rewardHandlerList;
	
	public SPDNModel(String filePath, String name, String id, List<ParameterHandler> parameterList, List<RewardHandler> rewardList) {
		this.filePath = filePath;
		this.name = name;
		this.id = id;
		this.parameterHandlerList = Collections.unmodifiableList(parameterList);
		this.rewardHandlerList = Collections.unmodifiableList(rewardList);
		
		this.parameterList = Collections.unmodifiableList(
				parameterHandlerList.stream()
									.map(handler -> handler.parameter)
									.collect(Collectors.toList()));
		
		this.rewardList = Collections.unmodifiableList(
				rewardHandlerList.stream()
									.map(handler -> handler.reward)
									.collect(Collectors.toList()));
	}
	
	public int getRewardNum() {
		return rewardHandlerList.size();
	}
	
	public double getSquareErrorOfReward(int idx, double result) {
		return rewardHandlerList.get(idx).getSqareError(result);
	}
	
	
}
