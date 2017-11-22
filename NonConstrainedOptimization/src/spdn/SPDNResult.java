package spdn;

import java.util.List;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;

public class SPDNResult {
	private double resultValue;
	private List<Parameter> params;
	private double[] paramValues;
	
	public SPDNResult(double[] result, double value, List<Parameter> params) {
		this.resultValue = value;
		this.params = params;
		this.paramValues = result;
	}
	
	public double getValue() { return resultValue; }
	
	public double[] getPoint() { return paramValues; }
	
	@Override
	public String toString() {
		String s = "Value = " + resultValue;
		for (int i = 0; i < params.size(); i++) {
			s += "\n" + params.get(i).getName() + " = " + paramValues[i];
		}
		return s;
	}
}
