package algorithms.bayes;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.linear.MatrixUtils;
import org.junit.Test;

import algorithms.bayes.FiniteDifferencesMethod;

public class FiniteDifferencesTest {
	
	@Test
	public void squareFuncTest() {
		FiniteDifferencesMethod method = new FiniteDifferencesMethod(vector -> {
			double result = 0;
			for (int i = 0; i < vector.getDimension(); i++) {
				result += Math.pow(vector.getEntry(i), 2);
			}
			return result;
		});
		double[] point = {1, 2, 3};
		double[] expectedDx = {2, 4, 6};
		double[] dx = method.df(MatrixUtils.createRealVector(point)).toArray();
		for (int i = 0; i < 3; i++) {
			assertEquals(expectedDx[i], dx[i], 1e-5);
		}
	}
}
