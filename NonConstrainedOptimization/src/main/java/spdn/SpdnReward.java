package spdn;

import hu.bme.mit.inf.petridotnet.spdn.Reward;

public class SpdnReward {
	public final String name;
	public final Reward reward;
	public final double expectedResult;
	
	public SpdnReward(String name, double expectedResult) {
		this.name = name;
		this.reward = Reward.instantaneous(name);
		this.expectedResult = expectedResult;
	}
	
	public double getSqareError(double result) {
		return Math.pow(result - expectedResult, 2);
	}
}
