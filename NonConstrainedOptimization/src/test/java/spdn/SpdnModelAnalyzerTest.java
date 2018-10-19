package spdn;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import model.TestModel;

@RunWith(Parameterized.class)
public class SpdnModelAnalyzerTest {
	
	private SpdnModel model;
	
	public SpdnModelAnalyzerTest(SpdnModel model) {
		this.model = model;
	}
	
	@Parameterized.Parameters
	public static Collection<SpdnModel> models() {
		return Arrays.asList(new SpdnModel[] {
				TestModel.SMPL.model(),
				TestModel.VCLS.model(),
				TestModel.HYBC.model(),
				TestModel.FIL5.model(),
				TestModel.FIL10.model(),
				TestModel.FIL15.model()
		});
	}
	
	
	@Test
	public void testModelWithDefaultValues() {
		SpdnModel model = TestModel.FIL9.model();
		SpdnModelAnalyzer analyzer = new SpdnModelAnalyzer(model);
		
		double objectResult = analyzer.calcObjective(model.getDefaultValues());
		assertEquals(objectResult, 0, 1e-5);
	}

}
