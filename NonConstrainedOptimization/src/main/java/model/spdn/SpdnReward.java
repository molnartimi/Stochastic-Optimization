package model.spdn;

import hu.bme.mit.inf.petridotnet.spdn.Reward;
import model.ModelReward;

public class SpdnReward extends ModelReward{
	public final Reward reward;
	
	public SpdnReward(String name, double expectedResult) {
		super(name, expectedResult);
		this.reward = Reward.instantaneous(name);
	}
}
