package model.storm;

import hu.bme.mit.modelchecker.storm.model.ModelParam;
import model.ModelParameter;

public class StormParameter extends ModelParameter {
	public final ModelParam parameter;
	
	public StormParameter(String name, double defaultValue, double minValue, double maxValue) {
		super(name, defaultValue, minValue, maxValue);
		this.parameter = new ModelParam(name);	
	}
}
