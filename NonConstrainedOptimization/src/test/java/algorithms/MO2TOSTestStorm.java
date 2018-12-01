package algorithms;

import javax.naming.directory.InvalidAttributeValueException;

import org.junit.Test;

import algorithms.mo2tos.MO2TOSHyperParam;
import algorithms.mo2tos.MO2TOSHyperParamBuilder;
import algorithms.mo2tos.MO2TOS_v0;
import hu.bme.mit.modelchecker.storm.exception.StormException;
import model.TestModel;

public class MO2TOSTestStorm extends BaseTest{
	@Test
	public void firstTest() throws InvalidAttributeValueException, StormException {
		MO2TOSHyperParam params = new MO2TOSHyperParamBuilder()
				.groupNumber(5)
				.heighModelSampleNumPerIter(20)
				.lowModelSampleNum(1000)
				.build();
		optimize(new MO2TOS_v0(TestModel.SMPL.stormModel()), params);
	}

}
