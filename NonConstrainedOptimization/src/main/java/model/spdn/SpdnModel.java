package model.spdn;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;
import model.Model;

public class SpdnModel extends Model<SpdnParameter, SpdnReward, SpdnModelAnalyzer> {
	
	public final transient List<Parameter> simpleParameterList;
	public final transient List<Reward> simpleRewardList;
	
	public SpdnModel(String filePath, String name, String id, List<SpdnParameter> parameterList, List<SpdnReward> rewardList) {
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
	protected SpdnModelAnalyzer createAnalyzer() {
		return new SpdnModelAnalyzer(this);
	}
}
