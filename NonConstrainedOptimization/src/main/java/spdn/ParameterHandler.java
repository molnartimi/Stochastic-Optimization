package spdn;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;

public class ParameterHandler {
	private static final Double THRESHOLD = 1e2;
	
	public final Parameter parameter;
	public final double defaultValue;
	public final double minValue, maxValue;

	public ParameterHandler(Parameter parameter, double defaultValue) {
		this.parameter = parameter;
		this.defaultValue = defaultValue;
		this.minValue = defaultValue / THRESHOLD;
		this.maxValue = defaultValue * THRESHOLD;
	}
}
