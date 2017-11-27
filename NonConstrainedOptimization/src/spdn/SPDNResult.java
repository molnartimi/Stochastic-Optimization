package spdn;

import java.util.List;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;

public class SPDNResult {
	private String algorithmID;
	private String modelID;
	private double resultValue;
	private List<Parameter> params;
	private double[] paramValues;
	
	public SPDNResult(double[] result, double value, List<Parameter> params, String aId, String mId) {
		this.resultValue = value;
		this.params = params;
		this.paramValues = result;
		this.algorithmID = aId;
		this.modelID = mId;
	}
	
	public double getValue() { return resultValue; }
	
	public double[] getPoint() { return paramValues; }
	
	@Override
	public String toString() {
		String s = "Algorithm: " + algorithmID + "\nModel: " + modelID + "\nValue = " + resultValue;
		for (int i = 0; i < params.size(); i++) {
			s += "\n" + params.get(i).getName() + " = " + paramValues[i];
		}
		return s;
	}
}
