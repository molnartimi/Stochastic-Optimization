package spdn.model;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;

public class SpdnParameter {
	private static final Double MIN_MAX_THRESHOLD = 1e2;
	
	public final String name;
	public final Parameter parameter;
	public final double defaultValue;
	public final double minValue, maxValue;

	public SpdnParameter(String name, double defaultValue) {
		this.name = name;
		this.parameter = Parameter.ofName(name);
		this.defaultValue = defaultValue;
		this.minValue = defaultValue / MIN_MAX_THRESHOLD;
		this.maxValue = defaultValue * MIN_MAX_THRESHOLD;
	}
}
