package algorithms;

import javax.naming.directory.InvalidAttributeValueException;

import org.junit.Test;

import algorithms.bayes.BayesHyperParam;
import algorithms.bayes.BayesHyperParamsBuilder;
import algorithms.bayes.BayesianOptimization;
import hu.bme.mit.modelchecker.storm.exception.StormException;
import model.TestModel;

public class BayesTest extends BaseTest {

	@Test
	public void fil3Test1() {
		BayesHyperParam params = new BayesHyperParamsBuilder(3).build();
		try {
			optimize(new BayesianOptimization(TestModel.FIL3.stormJaniModel()), params);
		} catch (InvalidAttributeValueException | StormException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
