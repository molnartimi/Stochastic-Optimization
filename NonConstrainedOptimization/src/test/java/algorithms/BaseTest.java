package algorithms;

import org.junit.Test;

import models.Model;

public abstract class BaseTest {
	
	@Test
	public void simpleServerTests() {
		test(Model.SIMPLE_SERVER);
	}
	
	@Test
	public void vclStochasticTests() {
		test(Model.VCL_STOCHASTIC);
	}

	@Test
	public void hybridCloudTests() {
		test(Model.HYBRID_CLOUD);
	}
	
	protected abstract void test(Model model);
}
