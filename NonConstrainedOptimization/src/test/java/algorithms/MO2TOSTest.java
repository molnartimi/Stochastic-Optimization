package algorithms;

import org.junit.Test;

import algorithms.mo2tos.MO2TOSHyperParam;
import algorithms.mo2tos.MO2TOSHyperParamBuilder;
import algorithms.mo2tos.MO2TOS_v0;

public class MO2TOSTest extends BaseTest {
	
	@Test
	public void firstTest() {
		MO2TOSHyperParam params = new MO2TOSHyperParamBuilder()
				.groupNumber(10)
				.heighModelSampleNumPerIter(20)
				.lowModelSampleNum(1000)
				.build();
		optimize(new MO2TOS_v0(model), params);
	}
}
