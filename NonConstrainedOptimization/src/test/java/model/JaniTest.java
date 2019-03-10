package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import hu.bme.mit.theta.interchange.jani.model.BoundedType;
import hu.bme.mit.theta.interchange.jani.model.ConstantDeclaration;
import hu.bme.mit.theta.interchange.jani.model.ConstantValue;
import hu.bme.mit.theta.interchange.jani.model.FilterExpression;
import hu.bme.mit.theta.interchange.jani.model.Identifier;
import hu.bme.mit.theta.interchange.jani.model.Property;
import hu.bme.mit.theta.interchange.jani.model.RealConstant;
import hu.bme.mit.theta.interchange.jani.model.RealType;
import hu.bme.mit.theta.interchange.jani.model.UnaryPropertyExpression;
import hu.bme.mit.theta.interchange.jani.model.json.JaniModelMapper;
import model.storm.StormModel;
import model.storm.StormParameter;
import model.storm.StormReward;

public class JaniTest {
	
	private hu.bme.mit.theta.interchange.jani.model.Model model;
	private final String PATH = "../STORM/models/philosophers_3_bounded.jani";
	private final String MODEL_NAME = "DiningPhilosophers";
	private final String MODEL_ID = "FIL3";
	
	@Before
	public void readJani() {
		try {
			model = new JaniModelMapper().readValue(new File(PATH), hu.bme.mit.theta.interchange.jani.model.Model.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deserializeTest() {
		StormModel stormModel = createStormModel();
		
		assertEquals(stormModel.parameterSize(), 3);
		assertEquals(stormModel.parameterList.get(0).minValue, 0, 1e-5);
		assertEquals(stormModel.parameterList.get(0).maxValue, 10, 1e-5);
		
		// assertEquals(rewards.get(0).name, "phil1_thinkingTime"); Reward name is 1 in JANI model
		assertEquals(stormModel.rewardSize(), 3);
		assertEquals(stormModel.rewardList.get(0).name, "phil1_thinkingTime");
		assertNotNull(stormModel);
	}
	
	@Test
	public void modifyJaniModelTest() {
		ArrayList<ConstantDeclaration> toDelete = new ArrayList<ConstantDeclaration>();
		ArrayList<ConstantDeclaration> toAdd = new ArrayList<ConstantDeclaration>();
		for (ConstantDeclaration c : model.getConstants()) {
			if (c.getValue() == null && c.getType() instanceof BoundedType) {
				toDelete.add(c);
				toAdd.add(new ConstantDeclaration(c.getName(), RealType.INSTANCE, null, null));
			}
		}
		model.getConstants().removeAll(toDelete);
		model.getConstants().addAll(toAdd);
		try {
			new JaniModelMapper().writeValue(new OutputStreamWriter(new FileOutputStream("../STORM/models/philosophers_3_bounded_modified.jani")), model);
		} catch (IOException e) {
			e.printStackTrace();
		}        
	}
	
	private StormModel createStormModel() {
		ArrayList<StormParameter> constants = new ArrayList<>();
		ArrayList<StormReward> rewards = new ArrayList<StormReward>();

		for (ConstantDeclaration c : model.getConstants()) {
			if (c.getValue() == null) {
				BoundedType type = (BoundedType) c.getType();
				RealConstant upper = (RealConstant) type.getUpperBound();
				RealConstant lower = (RealConstant) type.getLowerBound();
				constants.add(new StormParameter(c.getName(), /*TODO ilyen nem kéne bele*/ 3, lower.getValue(), upper.getValue()));
			}
		}
		
		for (Property reward : model.getProperties()) {
			if (reward.getExpression() instanceof FilterExpression) {
				FilterExpression filterExp = (FilterExpression) reward.getExpression();
				if (filterExp.getValues() instanceof UnaryPropertyExpression) {
					UnaryPropertyExpression exp = (UnaryPropertyExpression) filterExp.getValues();
					if (exp.getExp() instanceof Identifier) {
						Identifier rewardName = (Identifier) exp.getExp();
						rewards.add(new StormReward(rewardName.getName(), /*TODO nem kéne ide*/ 3));
					}
				}
			}
		}
		
		return new StormModel(PATH, MODEL_NAME, MODEL_ID, constants, rewards); 
	}

}
