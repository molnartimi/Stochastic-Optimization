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
import model.TestModel;
import spdn.analyzer.SpdnModelAnalyzer;
import spdn.model.SpdnModel;

@RunWith(Parameterized.class)
public class ToleranceTest {
	
	private SpdnModel model;
	private static PrintWriter csvWriter;
	private SpdnModelAnalyzer analyzer;
	
	private ArrayList<List<Double>> points;
	private final int numOfPoints = 50;
	private double[] tolerances = new double[] {1e-2, 1e-4, 1e-6, 1e-8, 1e-10};
	
	public ToleranceTest(SpdnModel model) {
		this.model = model;
		analyzer = new SpdnModelAnalyzer(model);
		generatePoints(model);
	}
	
	@Parameterized.Parameters
	public static Collection<SpdnModel> models() {
		return Arrays.asList(new SpdnModel[] {
				TestModel.SMPL.model(),
				TestModel.VCLS.model(),
				TestModel.HYBC.model(),
				TestModel.FIL3.model(),
				TestModel.FIL4.model(),
				TestModel.FIL5.model(),
				TestModel.FIL6.model(),
				TestModel.FIL7.model(),
				TestModel.FIL8.model(),
				TestModel.FIL9.model(),
				TestModel.FIL10.model(),
				TestModel.FIL11.model(),
				TestModel.FIL12.model(),
				TestModel.FIL13.model()/*,
				TestModel.FIL14.model(),
				TestModel.FIL15.model(),
				TestModel.FIL16.model()*/
		});
	}
	
	@BeforeClass
	public static void startCsv() {
		try {
			String name = System.getProperty("user.dir");
			name += (name.contains("\\") ? "\\..\\results\\" : "/../results/") + "all.csv";
			File f = new File(name);
			
			boolean exist = f.exists();
			csvWriter = new PrintWriter(new FileOutputStream(f, true));
			if (!exist) {
				csvWriter.append("model,tolerance,point_id,time,rewards,result\n");
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
				analyzer.setTolerance(tol);
				calcPoints(tol);
			} catch (RuntimeException e) {
				System.err.println("RuntimeException at: " + model.id + " - " + tol);
			}
		}
	}

	private void calcPoints(double tol) {
		int i = 0;
		for (List<Double> point : points) {
			double time = System.nanoTime();
			try {
				List<Double> results = analyzer.analyze(point);
				double objectResult = analyzer.calcObjective(point);
				logResult(tol, i, time, results, objectResult);
			} catch (SpdnException e) {
				System.err.println("Can't compute: " + model.id + " - " + point);
			} finally {
				i++;
			}
		}
	}

	private void logResult(double tol, int i, double time, List<Double> results, double objectResult) {
		StringJoiner joiner = new StringJoiner(",");
		String rowToLog = joiner.add(model.id)
								.add(Double.toString(tol))
								.add(Integer.toString(i))
								.add(Double.toString(System.nanoTime() - time))
								.add(results.stream().map(x -> Double.toString(x)).collect(Collectors.joining(";")))
								.add(Double.toString(objectResult))
								.toString();
		csvWriter.append(rowToLog + "\n");
	}

	public void generatePoints(SpdnModel model) {
		points = new ArrayList<>();
		for (int i = 0; i < numOfPoints; i++) {
			points.add(model.randomParamValues());
		}
	}

}
