package algorithms.bayes;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.junit.BeforeClass;
import org.junit.Test;

import algorithms.Sample;
import algorithms.ToleranceExceededException;

public class ExpectedImprovementTest {

	static public GaussProcess gp;
	static public NormalDistribution fi;
	
	@BeforeClass
	public static void initGp() {
		fi = new NormalDistribution();
		gp = new GaussProcess(1, Arrays.asList(1.0,1.0), 1e-3);
		Sample<List<Double>> point1 = new Sample<>(Arrays.asList(1.0, 2.0), 3.0);
		Sample<List<Double>> point2 = new Sample<>(Arrays.asList(3.0, 4.0), 7.0);
		
		try {
			gp.updateWithSample(point1);
			gp.updateWithSample(point2);
		} catch (ToleranceExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void valueTest() {
		ExpectedImprovement ei = new ExpectedImprovement(gp);
		RealVector checkPoint = MatrixUtils.createRealVector(new double[] {2.0, 3.0});
		assertEquals(1.65 + 0.98 * fi.density(1.6837) - 1.65 * fi.cumulativeProbability(1.6837), ei.calc(checkPoint), 1e-2);
	}
	
	@Test
	public void diffTest() {
		ExpectedImprovement ei = new ExpectedImprovement(gp);
		RealVector checkPoint = MatrixUtils.createRealVector(new double[] {2.0, 3.0});
		RealVector grad = ei.calcDx(checkPoint);
		// TODO assert?
	}
}
