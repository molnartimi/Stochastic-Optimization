package spdn;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;

public class SpdnParameter {
	private static final Double MIN_MAX_THRESHOLD = 1e2;
	
	public final Parameter parameter;
	public final double defaultValue;
	public final double minValue, maxValue;

	public SpdnParameter(Parameter parameter, double defaultValue) {
		this.parameter = parameter;
		this.defaultValue = defaultValue;
		this.minValue = defaultValue / MIN_MAX_THRESHOLD;
		this.maxValue = defaultValue * MIN_MAX_THRESHOLD;
	}
}
