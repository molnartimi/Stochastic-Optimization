package spdn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import models.Model;
import spdn.model.SpdnModel;
import spdn.model.SpdnParameter;

public class SPDNResult {
	private double resultValue;
	private List<Double> resultPoint;
	private String algorithmID;
	private SortedMap<String,Double> hyperParams;
	private SpdnModel model;
	private PrintWriter csvWriter = null;
	private long time;
	
	private final boolean DONT_WRITE_TO_CSV = true;
	
	public SPDNResult(double value, List<Double> result, String aId, SortedMap<String,Double> hyperParams, SpdnModel model) {
		this.resultValue = value;
		this.resultPoint = result;
		this.algorithmID = aId;
		this.model = model;
		this.hyperParams = hyperParams;
	}
	
	public double getValue() { return resultValue; }
	
	public List<Double> getPoint() { return resultPoint; }
	
	public void setTime(long time) { this.time = time; }
	
	@Override
	public String toString() {
		String s = "Done in " + time / 60. / 1000000000. + " min\nAlgorithm: " + algorithmID + "\nModel: " + model.id + "\nValue = " + resultValue;
		if (resultPoint == null) resultPoint = new ArrayList<>(model.parameterSize());
		for (int i = 0; i < model.parameterSize(); i++) {
			s += "\n" + model.simpleParameterList.get(i).getName() + " = " + resultPoint.get(i);
		}
		return s;
	}
	
	public void writeToCsv() {
		if (DONT_WRITE_TO_CSV) return;
		try {
			String name = System.getProperty("user.dir");
			File f;
			if (name.contains("\\")) {
				f = new File(name + "\\results\\" +algorithmID + "-" + model.id + ".csv");
			} else {
				f = new File(name + "/results/" +algorithmID + "-" + model.id + ".csv");
			}
			boolean exist = f.exists();
			csvWriter = new PrintWriter(new FileOutputStream(f, true));
			
			if (!exist) {
				writeCsvHeader();
			}
			
			writeResultToCsv();
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally{
			csvWriter.close();
		}
	}
	
	private void writeCsvHeader() {
		String header = "MIN VALUE;";
		for(SpdnParameter param: model.SpdnParameterList) {
			header += param.parameter.getName() + "(" + param.defaultValue + ");";
		}
		for(String key: hyperParams.keySet()) {
			header += "HP_" + key + ";";
		}
		header += "EXEC TIME(min)";
		csvWriter.append(header + "\n");
	}
	
	private void writeResultToCsv() {
		String row = String.valueOf(resultValue) + ";";
		if (resultPoint == null) resultPoint = new ArrayList<>(model.parameterSize());
		for(double x: resultPoint) {
			row += String.valueOf(x) + ";";
		}
		for(Double hyperparam: hyperParams.values()) {
			row += hyperparam + ";";
		}
		row += String.valueOf(time / 60. / 1000000000.);
		
		csvWriter.append(row + "\n");
	}
}
