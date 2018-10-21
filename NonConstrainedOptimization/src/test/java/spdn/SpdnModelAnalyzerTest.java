package spdn;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import model.TestModel;
import spdn.analyzer.SpdnModelAnalyzer;
import spdn.model.SpdnModel;

public class SpdnModelAnalyzerTest {
	
	@Test
	public void testModelWithDefaultValues() {
		SpdnModel model = TestModel.FIL9.model();
		SpdnModelAnalyzer analyzer = new SpdnModelAnalyzer(model);
		
		double objectResult = analyzer.calcObjective(model.getDefaultValues());
		assertEquals(objectResult, 0, 1e-5);
	}

}
