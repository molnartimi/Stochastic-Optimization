package model.storm;

import model.ModelReward;

public class StormReward extends ModelReward {
	public final hu.bme.mit.modelchecker.storm.model.ModelReward reward;
	public StormReward(String name, double expectedResult) {
		super(name, expectedResult);
		this.reward = new hu.bme.mit.modelchecker.storm.model.ModelReward(name);
	}

}
