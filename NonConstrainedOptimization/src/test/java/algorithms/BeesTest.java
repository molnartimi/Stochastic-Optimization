package algorithms;

import javax.naming.directory.InvalidAttributeValueException;

import org.junit.Test;

import algorithms.bees.BeesAlgorithm;
import algorithms.bees.BeesHyperParam;
import algorithms.bees.BeesHyperParamBuilder;
import hu.bme.mit.modelchecker.storm.exception.StormException;
import model.TestModel;

public class BeesTest extends BaseTest {

	@Test
	public void fil3Test1() {
		BeesHyperParam params = new BeesHyperParamBuilder().build();
		try {
			optimize(new BeesAlgorithm(TestModel.FIL3.stormJaniModel()), params);
		} catch (InvalidAttributeValueException | StormException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
