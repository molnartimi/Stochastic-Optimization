package algorithms.bayes;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import javax.naming.directory.InvalidAttributeValueException;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import algorithms.Sample;
import algorithms.helper.gd.GDHyperParam;
import algorithms.helper.gd.GDHyperParamBuilder;
import algorithms.helper.gd.GradientDescent;
import algorithms.helper.gd.GradientDescentException;

public class GradientDescentTest {

	@Test
	// Optimize f(x,y) = x^2 + y^2
	public void optimizeTest() throws GradientDescentException {
		GDHyperParam params = new GDHyperParamBuilder(MatrixUtils.createRealVector(new double[] { 1.0, 1.0 })).build();
		GradientDescent gd = new GradientDescent(vector -> {
			double sum = 0;
			for (int i = 0; i < vector.getDimension(); i++) {
				sum += Math.pow(vector.getEntry(i), 2);
			}
			return sum;
		}, vector -> {
			double[] d = new double[vector.getDimension()];
			for (int i = 0; i < vector.getDimension(); i++) {
				d[i] = 2 * vector.getEntry(i);
			}
			return MatrixUtils.createRealVector(d);
		}, () -> {
			Random r = new Random();
			double[] newVec = new double[] { r.nextDouble() * 5, r.nextDouble() * 3 };
			return MatrixUtils.createRealVector(newVec);
		}, vector -> false);
		Sample<RealVector> result = gd.optimize(params);

		assertEquals(0.0, result.point.getEntry(0), 1e-3);
		assertEquals(0.0, result.point.getEntry(1), 1e-3);
		assertEquals(0.0, result.value, 1e-3);
	}
}
