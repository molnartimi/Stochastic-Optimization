
package spdn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import hu.bme.mit.inf.optimization.wrapper.breeze.DiffFunction;
import hu.bme.mit.inf.optimization.wrapper.breeze.ValueAndGradient;
import hu.bme.mit.inf.petridotnet.spdn.*;
import models.Model;

public class SPDN implements DiffFunction {
	private Model model;
	private SpdnAnalyzer analyzer;
	private AnalysisBuilder builder;
	private List<Parameter> parameters;
	private List<Reward> rewards;
	private Map<Reward, Double> empiricalMeasurements;
	private ArrayList<String> countedValuesToCsv = new ArrayList<>();

	private final boolean DONT_WRITE_TO_CSV = true;

	public SPDN(Model model) {
		this.model = model;

		Spdn spdn = new Spdn("../../SPDN");
		analyzer = spdn.openModel("../SPDN/models/" + model.getFileName(), AnalysisConfiguration.DEFAULT);
		builder = analyzer.createAnalysisBuilder();

		parameters = model.getAllParams();
		rewards = model.getAllRewards();
		empiricalMeasurements = model.getAllMeasurements();
	}
	
	public SPDN(Model model, double tolerance) {
		this(model);
		setTolerance(tolerance);
	}
	
	public void setTolerance(double tol) {
		analyzer.setTolerance(tol);
	}

	public double f(double[] variables) {
		return f(MatrixUtils.createRealVector(variables));
	}

	public double f(RealVector variables) throws SpdnException {
		Double fx = null;
		try {
			AnalysisResult result = runAnalyzer(variables, false);
			fx = calcObjectiveF(result);
		} catch (SpdnException e) {
			throw e;
		} finally {
			saveCountedValues(fx, variables);
		}
		return fx;
	}

	public RealVector df(double[] variables) {
		return df(MatrixUtils.createRealVector(variables));
	}

	public RealVector df(RealVector variables) throws SpdnException {
		Double fx = null;
		RealVector dfx = null;
		try {
			AnalysisResult result = runAnalyzer(variables, true);
			fx = calcObjectiveF(result);
			dfx = calcObjectiveDf(result, variables);
		} catch (SpdnException e) {
			throw e;
		} finally {
			saveCountedValues(fx, variables);
		}
		return dfx;
	}

	@Override
	public ValueAndGradient calculate(double[] x) throws SpdnException {
		Double fx = null;
		RealVector dfx = null;
		RealVector variables = MatrixUtils.createRealVector(x);
		try {
			AnalysisResult result = runAnalyzer(variables, true);
			fx = calcObjectiveF(result);
			dfx = calcObjectiveDf(result, variables);
		} catch (SpdnException e) {
			throw e;
		} finally {
			saveCountedValues(fx, variables);
		}
		return new ValueAndGradient(fx, dfx.toArray());
	}

	public static RealVector convertPoint(RealVector v) {
		RealVector result = MatrixUtils.createRealVector(new double[v.getDimension()]);
		for (int i = 0; i < v.getDimension(); i++)
			result.setEntry(i, Math.exp(v.getEntry(i)));
		return result;
	}

	public int getDimension() {
		return parameters.size();
	}

	public void writeCountedDataToCsv(String algorithmId) {
		if (DONT_WRITE_TO_CSV)
			return;
		PrintWriter csvWriter = null;
		try {
			Random r = new Random();
			int randId = (int) (r.nextDouble() * 10000);
			String name = System.getProperty("user.dir");
			File f;
			if (name.contains("\\")) {
				f = new File(name + "\\src\\spdn\\results\\" + model.getId() + "_DATAS_points.csv");
			} else {
				f = new File(
						name + "/NonConstrainedOptimization/src/spdn/results/" + model.getId() + "_DATAS_points.csv");
			}
			boolean exist = f.exists();
			csvWriter = new PrintWriter(new FileOutputStream(f, true));

			if (!exist) {
				String header = "Random_ID;Feasibility;Obj_func_value;";
				for (Parameter param : parameters) {
					header += param.getName() + ";";
				}
				header += "\n";
				csvWriter.append(header);
			}

			for (String row : countedValuesToCsv) {
				csvWriter.append(algorithmId + randId + ";" + row + "\n");
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			csvWriter.close();
		}
	}

	private AnalysisResult runAnalyzer(RealVector variables, boolean derivatives) throws SpdnException {
		setRewards(derivatives);
		setParams(variables);
		AnalysisResult result = builder.run();
		return result;
	}

	private void setRewards(boolean derivatives) {
		for (int i = 0; i < rewards.size(); i++) {
			builder.withReward(rewards.get(i), derivatives ? parameters : new ArrayList<Parameter>());
		}
	}

	private void setParams(RealVector variables) {
		for (int i = 0; i < parameters.size(); i++) {
			builder = builder.withParameter(parameters.get(i), Math.exp(variables.getEntry(i)));
		}
	}

	private double calcObjectiveF(AnalysisResult result) {
		double resultF = 0;
		for (int i = 0; i < rewards.size(); i++) {
			resultF += Math.pow(empiricalMeasurements.get(rewards.get(i)) - result.getValue(rewards.get(i)), 2);
		}

		return resultF;
	}

	private RealVector calcObjectiveDf(AnalysisResult result, RealVector variables) {
		double[] fDResult = new double[getDimension()];

		for (int i = 0; i < fDResult.length; i++) {
			for (int j = 0; j < rewards.size(); j++) {
				fDResult[i] += -2 * (empiricalMeasurements.get(rewards.get(j)) - result.getValue(rewards.get(j)))
						* result.getSensitivity(rewards.get(j), parameters.get(i)) * Math.exp(variables.getEntry(i));
			}
		}

		return MatrixUtils.createRealVector(fDResult);
	}

	private void saveCountedValues(Double value, RealVector variables) {
		String csvRow = value == null ? "-1;ERROR;" : "1;" + String.valueOf(value) + ";";
		RealVector convertedVariables = convertPoint(variables);
		for (int i = 0; i < variables.getDimension(); i++)
			csvRow += String.valueOf(convertedVariables.getEntry(i)) + ";";
		countedValuesToCsv.add(csvRow);
	}
}
