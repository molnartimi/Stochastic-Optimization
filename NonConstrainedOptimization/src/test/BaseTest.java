package test;

import org.junit.Test;

import models.Models;

public abstract class BaseTest {
	
	@Test
	public void simpleServerTests() {
		test(Models.SIMPLE_SERVER);
	}
	
	@Test
	public void vclStochasticTests() {
		test(Models.VCL_STOCHASTIC);
	}

	@Test
	public void hybridCloudTests() {
		test(Models.HYBRID_CLOUD);
	}
	
	protected abstract void test(Models model);
}
