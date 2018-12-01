package spdn;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import model.TestModel;
import model.spdn.SpdnModel;
import model.spdn.SpdnModelAnalyzer;

public class SpdnModelAnalyzerTest {
	
	@Test
	public void testModelWithDefaultValues() {
		SpdnModel model = TestModel.FIL9.spdnModel();
		SpdnModelAnalyzer analyzer = new SpdnModelAnalyzer(model);
		
		double objectResult = analyzer.calcObjective(model.getDefaultValues());
		assertEquals(objectResult, 0, 1e-5);
	}

}
