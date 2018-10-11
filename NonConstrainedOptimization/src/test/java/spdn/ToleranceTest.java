package spdn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;

public class ToleranceTest {
	
	public ArrayList<double[]> points;
	public final int numOfPoints = 100;
	public static PrintWriter csvWriter;
	public double[] tolerances = new double[] {1e-2, 1e-4, 1e-6, 1e-8, 1e-10, 1e-12};
	
	@BeforeClass
	public static void startCsv() {
		try {
			String name = System.getProperty("user.dir");
			name += (name.contains("\\") ? "\\..\\results\\" : "/../results/") + "tolerance_tests.csv";
			File f = new File(name);
			
			boolean exist = f.exists();
			ToleranceTest.csvWriter = new PrintWriter(new FileOutputStream(f, true));
			if (!exist) {
				ToleranceTest.csvWriter.append("model,tolerance,point_id,time,value\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void exitCsv() {
		if (csvWriter != null)
			csvWriter.close();
	}
	
	public void generatePoints(Model model) {
		points = new ArrayList<>();
		for (int i = 0; i < numOfPoints; i++) {
			points.add(model.getRandomPoint());
		}
	}
	
	public void runAll(Model model) {
		generatePoints(model);
		SPDN spdn = new SPDN(model);
		double result = 0;
		for (double tol: tolerances) {
			try {
				spdn.setTolerance(tol);
				int i = 0;
				for (double[] point : points) {
					double time = System.nanoTime();
					try {
						result = spdn.f(point);
						csvWriter.append(model.getId() + "," +
								tol + "," +
								i + "," +
								(System.nanoTime() - time) + "," +
								result + "\n");
						result = 0;
					} catch (SpdnException e) {
						System.err.println("Can't compute: " + model.getId() + " - " + point.toString());
					} finally {
						i++;
					}
				}
			} catch (RuntimeException e) {
				System.err.println("RuntimeException at: " + model.getId() + " - " + tol);
			}
		}
	}
	
	@Test
	public void runMultipleSimpleServer() {
		runAll(Model.SIMPLE_SERVER);
	}
	
	@Test
	@Ignore("Fails too many times")
	public void runHybridCloud() {
		runAll(Model.HYBRID_CLOUD);
	}
	
	@Test
	public void runVCLStochastic() {
		runAll(Model.VCL_STOCHASTIC);
	}
	
	@Test
	public void runFil3() {
		runAll(Model.FIL3);
	}
	
	@Test
	public void runFil5() {
		runAll(Model.FIL5);
	}
	
	@Test
	public void runFil7() {
		runAll(Model.FIL7);
	}
	
	@Test
	public void runFil9() {
		runAll(Model.FIL9);
	}

}
