package spdn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.SortedMap;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import models.Model;

public class SPDNResult {
	private double resultValue;
	private double[] resultPoint;
	private String algorithmID;
	private SortedMap<String,Double> hyperParams;
	private Model model;
	private PrintWriter csvWriter = null;
	
	public SPDNResult(double value, double[] result, String aId, SortedMap<String,Double> hyperParams, Model model) {
		this.resultValue = value;
		this.resultPoint = result;
		this.algorithmID = aId;
		this.model = model;
		this.hyperParams = hyperParams;
	}
	
	public double getValue() { return resultValue; }
	
	public double[] getPoint() { return resultPoint; }
	
	@Override
	public String toString() {
		String s = "Algorithm: " + algorithmID + "\nModel: " + model.getId() + "\nValue = " + resultValue;
		for (int i = 0; i < model.getDim(); i++) {
			s += "\n" + model.getParam(i).getName() + " = " + resultPoint[i];
		}
		return s;
	}
	
	public void writeToCsv() {
		try {
			String name = System.getProperty("user.dir");
			File f = new File(name + "\\src\\spdn\\results\\" +algorithmID + "-" + model.getId() + ".csv");
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
		for(Parameter param: model.getAllParams()) {
			header += param.getName() + "(" + model.getValidValue(param) + ");";
		}
		for(String key: hyperParams.keySet()) {
			header += "HP_" + key + ";";
		}
		
		csvWriter.append(header + "\n");
	}
	
	private void writeResultToCsv() {
		String row = String.valueOf(resultValue) + ";";
		for(double x: resultPoint) {
			row += String.valueOf(x) + ";";
		}
		for(Double hyperparam: hyperParams.values()) {
			row += hyperparam + ";";
		}
		
		csvWriter.append(row + "\n");
	}
}
