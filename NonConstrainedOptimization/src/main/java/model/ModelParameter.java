package model;

public class ModelParameter {
	private static final Double MIN_MAX_THRESHOLD = 1e2;
	
	public final String name;
	public final double defaultValue;
	public final double minValue, maxValue;

	public ModelParameter(String name, double defaultValue, double minValue, double maxValue) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public ModelParameter(String name, double defaultValue) {
		this(name, defaultValue, defaultValue / MIN_MAX_THRESHOLD, defaultValue * MIN_MAX_THRESHOLD);
	}
}
