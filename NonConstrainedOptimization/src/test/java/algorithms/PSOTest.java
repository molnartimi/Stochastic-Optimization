package algorithms;

import javax.naming.directory.InvalidAttributeValueException;

import org.junit.Test;

import algorithms.pso.PSOHyperParam;
import algorithms.pso.PSOHyperParamBuilder;
import algorithms.pso.ParticleSwarm;
import hu.bme.mit.modelchecker.storm.exception.StormException;
import model.TestModel;

public class PSOTest extends BaseTest {

	@Test
	public void fil3Test1() {
		PSOHyperParam params = new PSOHyperParamBuilder().build();
		try {
			optimize(new ParticleSwarm(TestModel.FIL3.stormJaniModel()), params);
		} catch (InvalidAttributeValueException | StormException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
