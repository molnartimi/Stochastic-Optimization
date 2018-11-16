package model;

public class ModelReward {
	public final String name;
	public final double expectedResult;
	
	public ModelReward(String name, double expectedResult) {
		this.name = name;
		this.expectedResult = expectedResult;
	}
	
	public double getSqareError(double result) {
		return Math.pow(result - expectedResult, 2);
	}
}
