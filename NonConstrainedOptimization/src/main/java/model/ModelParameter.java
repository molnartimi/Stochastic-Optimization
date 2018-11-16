package model;

public class ModelParameter {
	private static final Double MIN_MAX_THRESHOLD = 1e2;
	
	public final String name;
	public final double defaultValue;
	public final double minValue, maxValue;

	public ModelParameter(String name, double defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.minValue = defaultValue / MIN_MAX_THRESHOLD;
		this.maxValue = defaultValue * MIN_MAX_THRESHOLD;
	}
}
