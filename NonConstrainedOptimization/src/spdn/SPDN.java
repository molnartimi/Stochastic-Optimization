package spdn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import hu.bme.mit.inf.optimization.wrapper.breeze.DiffFunction;
import hu.bme.mit.inf.optimization.wrapper.breeze.ValueAndGradient;
import hu.bme.mit.inf.petridotnet.spdn.AnalysisBuilder;
import hu.bme.mit.inf.petridotnet.spdn.AnalysisConfiguration;
import hu.bme.mit.inf.petridotnet.spdn.AnalysisResult;
import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;
import hu.bme.mit.inf.petridotnet.spdn.Spdn;
import hu.bme.mit.inf.petridotnet.spdn.SpdnAnalyzer;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Models;

public class SPDN implements DiffFunction {
	
	private SpdnAnalyzer analyzer;
	private AnalysisBuilder builder;
	private List<Parameter> parameters;
	private List<Reward> rewards;
	private Map<Reward,Double> empiricalMeasurements;
	private double errorValue;
	
	public SPDN(Models model, double errorValue) {
		Spdn spdn = new Spdn("../../SPDN");
		analyzer = spdn.openModel("../SPDN/models/" + model.getFileName(), AnalysisConfiguration.DEFAULT);
		parameters = model.getAllParams();
		rewards = model.getAllRewards();
		empiricalMeasurements = model.getAllMeasurements();
		builder = analyzer.createAnalysisBuilder();
		this.errorValue = errorValue > 0 ? errorValue : 1000000;
	}
	
	public double f(double[] variables) {
		return f(MatrixUtils.createRealVector(variables));
	}
	
	public double f(RealVector variables) {		
		try {
			AnalysisResult result = runAnalyzer(variables, false);
			return calcObjectiveF(result);
		} catch(SpdnException e) {
			return errorValue;
		}
	}
	
	public static RealVector convertPoint(RealVector v){ 
	    RealVector result = MatrixUtils.createRealVector(new double[v.getDimension()]); 
	    for(int i = 0; i < v.getDimension(); i++) 
	      result.setEntry(i, Math.exp(v.getEntry(i))); 
	    return result; 
	} 
	
	public RealVector Df(double[] variables) {
		return Df(MatrixUtils.createRealVector(variables));
	}

	public RealVector Df(RealVector variables) {		
        try {
        	AnalysisResult result = runAnalyzer(variables, true);
        	return calcObjectiveDf(result, variables);
        } catch(SpdnException e) {
        	double[] errorGradient = new double[getDimension()];
        	Arrays.fill(errorGradient, errorValue);
        	return MatrixUtils.createRealVector(errorGradient);
        }
        
	}
	
	@Override
	public ValueAndGradient calculate(double[] x) throws SpdnException {
		RealVector variables = MatrixUtils.createRealVector(x);
		try{
			AnalysisResult result = runAnalyzer(variables, true);
			return new ValueAndGradient(calcObjectiveF(result), calcObjectiveDf(result, variables).toArray());
		} catch(SpdnException e) {
			double[] errorGradient = new double[getDimension()];
        	Arrays.fill(errorGradient, errorValue);
        	return new ValueAndGradient(errorValue, errorGradient);
		}
	}
		
	
	public int getDimension() {
		return parameters.size();
	}
	
	private AnalysisResult runAnalyzer(RealVector variables, boolean derivatives) throws SpdnException{
        setRewards(derivatives);
		setParams(variables);
        AnalysisResult result = builder.run();
        return result;
	}
		
	private void setRewards(boolean derivatives) {
		for(int i=0; i<rewards.size(); i++){
			builder.withReward(rewards.get(i), derivatives ? parameters : new ArrayList<Parameter>());
		}	
	}

	private void setParams(RealVector variables) {
		for(int i=0; i<parameters.size(); i++){
			builder = builder.withParameter(parameters.get(i), Math.exp(variables.getEntry(i)));
		}
	}
	
	private double calcObjectiveF(AnalysisResult result) {
		double resultF = 0;
		for(int i=0; i<rewards.size(); i++){
			resultF += Math.pow(empiricalMeasurements.get(rewards.get(i)) - result.getValue(rewards.get(i)), 2);
		}
		
		return resultF;
	}

	private RealVector calcObjectiveDf(AnalysisResult result, RealVector variables) {
		double[] fDResult = new double[getDimension()];
		
		for (int i=0; i<fDResult.length; i++){
			for(int j=0; j<rewards.size(); j++){
				fDResult[i] += -2 * (empiricalMeasurements.get(rewards.get(j)) - result.getValue(rewards.get(j))) * 
						result.getSensitivity(rewards.get(j), parameters.get(i)) * 
						Math.exp(variables.getEntry(i));
			}
		}

        return MatrixUtils.createRealVector(fDResult);
	}
	

}
