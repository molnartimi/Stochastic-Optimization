package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.directory.InvalidAttributeValueException;

import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;

import hu.bme.mit.modelchecker.storm.exception.StormException;
import model.storm.StormModel;

public class StormModelFactoryTest {
	
	@Test
	public void simpleServerTest() {
		StormModel model;
		try {
			model = TestModel.SMPL.stormModel();
			assertEquals(model.id, "SMPL");
			assertEquals(model.name, "Net1");
			
			List<String> paramNames = model.simpleParameterList.stream().map(param -> param.name).collect(Collectors.toList());
			List<String> expectedParamNames = Arrays.asList(new String[] {"requestRate", "serviceTime"});
			assertThat(paramNames, Is.is(expectedParamNames));
			
			List<Double> defaultValues = model.parameterList.stream().map(handler -> handler.defaultValue).collect(Collectors.toList());
			List<Double> expectedDefaultValues = Arrays.asList(new Double[] {1.5, 0.25});
			assertThat(defaultValues, Is.is(expectedDefaultValues));
			
			List<String> rewardNames = model.simpleRewardList.stream().map(reward -> reward.name).collect(Collectors.toList());
			List<String> expectedRewardNames = Arrays.asList(new String[] {"Idle", "ServedRequests"});
			assertThat(rewardNames, Is.is(expectedRewardNames));
		} catch (InvalidAttributeValueException | StormException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	// @Ignore("Check now jani version")
	public void fil3Test() throws InvalidAttributeValueException, StormException {
		StormModel model = TestModel.FIL3.stormModel();
			
		assertEquals(model.name, "DiningPhilosophers");
			
		List<String> paramNames = model.simpleParameterList.stream().map(param -> param.name).collect(Collectors.toList());
		List<String> expectedParamNames = Arrays.asList(new String[] {"phil1_eatingRate", "phil2_eatingRate", "phil3_eatingRate"});
		assertThat(paramNames, Is.is(expectedParamNames));
			
		List<Double> defaultValues = model.parameterList.stream().map(handler -> handler.defaultValue).collect(Collectors.toList());
		List<Double> expectedDefaultValues = Arrays.asList(new Double[] {7.33569408796258, 2.156376928966199, 9.680783503298795});
		assertThat(defaultValues, Is.is(expectedDefaultValues));
			
		List<String> rewardNames = model.simpleRewardList.stream().map(reward -> reward.name).collect(Collectors.toList());
		List<String> expectedRewardNames = Arrays.asList(new String[] {"phil1_thinkingTime", "phil2_thinkingTime", "phil3_thinkingTime"});
		assertThat(rewardNames, Is.is(expectedRewardNames));
	}
	
	@Test
	public void fil3JaniTest() throws InvalidAttributeValueException, StormException {
		StormModel model = TestModel.FIL3.stormJaniModel();
			
		assertEquals(model.name, "DiningPhilosophers");
			
		List<String> paramNames = model.simpleParameterList.stream().map(param -> param.name).collect(Collectors.toList());
		List<String> expectedParamNames = Arrays.asList(new String[] {"phil1_eatingRate", "phil2_eatingRate", "phil3_eatingRate"});
		assertThat(paramNames, Is.is(expectedParamNames));
			
		List<Double> defaultValues = model.parameterList.stream().map(handler -> handler.defaultValue).collect(Collectors.toList());
		List<Double> expectedDefaultValues = Arrays.asList(new Double[] {7.33569408796258, 2.156376928966199, 9.680783503298795});
		assertThat(defaultValues, Is.is(expectedDefaultValues));
			
		List<String> rewardNames = model.simpleRewardList.stream().map(reward -> reward.name).collect(Collectors.toList());
		List<String> expectedRewardNames = Arrays.asList(new String[] {"phil1_thinkingTime", "phil2_thinkingTime", "phil3_thinkingTime"});
		assertThat(rewardNames, Is.is(expectedRewardNames));
	}

}
