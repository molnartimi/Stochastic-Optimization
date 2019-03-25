package algorithms.bayes;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import algorithms.Sample;
import algorithms.ToleranceExceededException;

public class GaussProcessTest {

	@Test
	// for function f(x,y) = x+y
	public void functionTest() {
		GaussProcess gp = new GaussProcess(1, Arrays.asList(1.0,1.0), 1e-3);
		try {
			Sample<List<Double>> point1 = new Sample<>(Arrays.asList(1.0, 2.0), 3.0);
			Sample<List<Double>> point2 = new Sample<>(Arrays.asList(3.0, 4.0), 7.0);
			
			gp.updateWithSample(point1);
			gp.updateWithSample(point2);

			RealVector checkPoint = MatrixUtils.createRealVector(new double[] {2.0, 3.0});
			assertEquals(1.3533, gp.getMean(checkPoint), 1e-2);
			assertEquals(0.9634, gp.getVariance(checkPoint), 1e-1);
		} catch (NoDataException | NullArgumentException | ToleranceExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
