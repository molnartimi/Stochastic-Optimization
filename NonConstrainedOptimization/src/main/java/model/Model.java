package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import umontreal.ssj.hups.LatinHypercube;
import umontreal.ssj.rng.MRG32k3a;

public abstract class Model<P extends ModelParameter, R extends ModelReward, A extends ModelChecker<?,?>>{
	public final String filePath;
	public final String name;
	public final String id;
	public final List<P> parameterList;
	public final List<R> rewardList;
	private A analyzer;
	
	public Model(String filePath, String name, String id, List<P> parameterList, List<R> rewardList) {
		this.filePath = filePath;
		this.name = name;
		this.id = id;
		this.parameterList = Collections.unmodifiableList(parameterList);
		this.rewardList = Collections.unmodifiableList(rewardList);
	}
	
	public A getInstanceOfChecker() {
		if (analyzer == null) {
			analyzer = createAnalyzer();
		}
		return analyzer;
	}
	
	public int rewardSize() {
		return rewardList.size();
	}
	
	public int parameterSize() {
		return parameterList.size();
	}
	
	public double getSquareErrorOfReward(int idx, double result) {
		return rewardList.get(idx).getSqareError(result);
	}

	public List<Double> getDefaultValues() {
		return parameterList.stream().map(param -> param.defaultValue).collect(Collectors.toList());
	}

	public List<Double> randomParamValues() {
		List<Double> point = new ArrayList<>();
		Random rand = new Random();
		
		for(P param: parameterList) {
			point.add(rand.nextDouble() * (param.maxValue - param.minValue) + param.minValue);
		}
		
		return point;
	}
	
	public RealVector randomVector() {
		double[] vec = new double[parameterSize()];
		Random rand = new Random();
		
		for(int i = 0; i < parameterSize(); i++) {
			P param = parameterList.get(i);
			vec[i] = (rand.nextDouble() * (param.maxValue - param.minValue) + param.minValue);
		}
		return MatrixUtils.createRealVector(vec);
	}
	
	public List<Double> randomVelocity() {
		List<Double> point = new ArrayList<>();
		Random rand = new Random();
		
		for(P param: parameterList) {
			double area = param.maxValue - param.minValue;
			point.add(rand.nextDouble() * 2 * area - area);
		}
		
		return point;
	}
	
	public List<List<Double>> latinHypercubeParamValues(int n) {
		LatinHypercube cube = new LatinHypercube(n, parameterSize());
		cube.randomize(new MRG32k3a());
		return convertLatinHypercubeResult(cube.getArray());
	}
	
	public void cutParamsOnBorder(List<Double> paramValues) {
		for (int i = 0; i < parameterSize(); i++) {
			P modelParam = parameterList.get(i);
			if (paramValues.get(i) < modelParam.minValue)
				paramValues.set(i, modelParam.minValue);
			else if (paramValues.get(i) > modelParam.maxValue)
				paramValues.set(i, modelParam.maxValue);
		}
	}
	
	public boolean isOutOfBound(RealVector vector) {
		for (int i = 0; i < parameterSize(); i++) {
			P modelParam = parameterList.get(i);
			if (vector.getEntry(i) < modelParam.minValue || modelParam.maxValue < vector.getEntry(i))
				return true;
		}
		return false;
	}

	/**
	 * Convert double[][] array which returned by LatinHypercube into List<List<Double>>, with valid parameter values (not between 0 and 1)
	 * 
	 * @param valuesArray
	 * @return
	 */
	private List<List<Double>> convertLatinHypercubeResult(double[][] valuesArray) {
		List<List<Double>> points = new ArrayList<>();
		for (int i = 0; i < valuesArray.length; i++) {
			List<Double> values = new ArrayList<>();
			for (int j = 0; j < parameterSize(); j++) {
				values.add(getRandomParamValueFromNormal(j, valuesArray[i][j]));
			}
			points.add(values);
		}
		return points;
	}

	/**
	 * Calculate the random param value form normalized value to value between parameter borders.
	 * 
	 * @param paramIdx - index of parameter in list of parameters
	 * @param normalValue - 0 <= normalValue < 1
	 * @return
	 */
	private Double getRandomParamValueFromNormal(int paramIdx, double normalValue) {
		P param = parameterList.get(paramIdx);
		return (normalValue * (param.maxValue - param.minValue) + param.minValue);
	}
	
	protected abstract A createAnalyzer();

}
