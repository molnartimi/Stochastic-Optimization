package algorithms.msc;

import org.junit.Test;

import algorithms.BaseTest;
import algorithms.msc.mo2tos.MO2TOSHyperParam;
import algorithms.msc.mo2tos.MO2TOSHyperParamBuilder;
import algorithms.msc.mo2tos.MO2TOS_v0;

public class MO2TOSTest extends BaseTest {
	
	@Test
	public void firstTest() {
		MO2TOSHyperParam params = new MO2TOSHyperParamBuilder()
				.groupNumber(5)
				.heighModelSampleNumPerIter(15)
				.lowModelSampleNum(100)
				.build();
		optimize(new MO2TOS_v0(model), params);
	}
}
