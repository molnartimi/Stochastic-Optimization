package model.storm;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.bme.mit.modelchecker.storm.model.ModelParam;
import hu.bme.mit.modelchecker.storm.model.ModelReward;
import model.Model;

public class StormModel extends Model<StormParameter, StormReward, StormAnalyzer>{
	public final List<ModelParam> simpleParameterList;
	public final List<ModelReward> simpleRewardList;

	public StormModel(String filePath, String name, String id, List<StormParameter> parameterList,
			List<StormReward> rewardList) {
		super(filePath, name, id, parameterList, rewardList);
		this.simpleParameterList = Collections.unmodifiableList(
				parameterList.stream()
									.map(handler -> handler.parameter)
									.collect(Collectors.toList()));
		
		this.simpleRewardList = Collections.unmodifiableList(
				rewardList.stream()
									.map(handler -> handler.reward)
									.collect(Collectors.toList()));
	}

	@Override
	protected StormAnalyzer createAnalyzer() {
		return new StormAnalyzer(this);
	}

}
