package model.spdn;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import model.ModelParameter;

public class SpdnParameter extends ModelParameter {
	public final Parameter parameter;

	public SpdnParameter(String name, double defaultValue) {
		super(name, defaultValue);
		this.parameter = Parameter.ofName(name);
	}
}
