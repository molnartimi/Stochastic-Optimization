package spdn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;

@RunWith(Parameterized.class)
public class SimpleToleranceTest {
	
	private Model model;
	private static PrintWriter csvWriter;
	private ArrayList<List<Double>> points;
	private final int numOfPoints = 100;
	private double[] tolerances = new double[] {1e-2, 1e-4, 1e-6, 1e-8, 1e-10, 1e-12};
	private SPDNModelExecutor spdn;
	
	/*public SimpleToleranceTest(Model model) {
		this.model = model;
		spdn = new SPDNModelExecutor(model);
		generatePoints(model);
	}
	
	@Parameterized.Parameters
	public static Collection<Model> models() {
		return Arrays.asList(new Model[] 
				{Model.SIMPLE_SERVER, Model.VCL_STOCHASTIC, Model.HYBRID_CLOUD,
				 Model.FIL3, Model.FIL5, Model.FIL7, Model.FIL9});
	}
	
	@BeforeClass
	public static void startCsv() {
		try {
			String name = System.getProperty("user.dir");
			name += (name.contains("\\") ? "\\..\\results\\" : "/../results/") + "simple_tolerance_tests.csv";
			File f = new File(name);
			
			boolean exist = f.exists();
			csvWriter = new PrintWriter(new FileOutputStream(f, true));
			if (!exist) {
				csvWriter.append("model,tolerance,point_id,time,values,objective\n");
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

	@Test
	public void runTest() {
		for (double tol: tolerances) {
			try {
				spdn.setTolerance(tol);
				int i = 0;
				for (List<Double> point : points) {
					double time = System.nanoTime();
					try {
						List<Double> results = spdn.calcRewardsAtPoint(point);
						StringJoiner joiner = new StringJoiner(",");
						String rowToLog = joiner.add(model.getId())
												.add(Double.toString(tol))
												.add(Integer.toString(i))
												.add(Double.toString(System.nanoTime() - time))
												.add(point.stream().map(x -> Double.toString(x)).collect(Collectors.joining(";")))
												.add(arg0)
												.toString();
						csvWriter.append(rowToLog + "\n");
						results = null;
					} catch (SpdnException e) {
						System.err.println("Can't compute: " + model.getId() + " - " + point);
					} finally {
						i++;
					}
				}
			} catch (RuntimeException e) {
				System.err.println("RuntimeException at: " + model.getId() + " - " + tol);
			}
		}
	}

	public void generatePoints(Model model) {
		points = new ArrayList<>();
		for (int i = 0; i < numOfPoints; i++) {
			points.add(model.getRandomPointList());
		}
	}*/

}
