package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import model.spdn.SpdnModel;
import model.spdn.SpdnParameter;

public class ModelTest {

	@Test
	public void latinHyperCubeTest() {
		SpdnModel model = TestModel.FIL5.spdnModel();
		int n = 10;
		List<List<Double>> randomParams = model.latinHypercubeParamValues(n);
		
		assertEquals(n, randomParams.size());
		assertEquals(model.parameterSize(), randomParams.get(0).size());
		
		validateRandomParams(model.parameterList, randomParams);
	}

	private void validateRandomParams(List<SpdnParameter> parameters, List<List<Double>> randomParams) {
		int n = randomParams.size();
		for (int i = 0; i < n; i++) {
			List<Double> point = randomParams.get(i);
			for (int j = 0; j < point.size(); j++) {
				SpdnParameter spdnParam = parameters.get(j);
				double value = point.get(j);
				assertTrue(value >= spdnParam.minValue);
				assertTrue(value < spdnParam.maxValue);
				if (i > 0) {
					// Check if LatinHypercube works well
					double prevPointParamValue = randomParams.get(i - 1).get(j);
					double minDiff = (spdnParam.maxValue - spdnParam.minValue) / n;
					System.out.println(spdnParam.name + ": |" + prevPointParamValue + " - " + value + "| = " + Math.abs(prevPointParamValue - value) + " > " + (minDiff));
					// NOT ALWAYS TRUE
					// assertTrue(Math.abs(value - prevPointParamValue) >= minDiff);
				}
			}
		}
		
	}
}
