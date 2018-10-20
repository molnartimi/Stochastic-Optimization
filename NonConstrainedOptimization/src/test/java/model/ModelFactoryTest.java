package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.core.Is;
import org.junit.Test;

import spdn.model.SpdnModel;

public class ModelFactoryTest {
	
	@Test
	public void simpleServerTest() {
		SpdnModel model = TestModel.SMPL.model();
		
		assertEquals(model.id, "SMPL");
		assertEquals(model.name, "Net1");
		
		List<String> paramNames = model.simpleParameterList.stream().map(param -> param.getName()).collect(Collectors.toList());
		List<String> expectedParamNames = Arrays.asList(new String[] {"requestRate", "serviceTime"});
		assertThat(paramNames, Is.is(expectedParamNames));
		
		List<Double> defaultValues = model.SpdnParameterList.stream().map(handler -> handler.defaultValue).collect(Collectors.toList());
		List<Double> expectedDefaultValues = Arrays.asList(new Double[] {1.5, 0.25});
		assertThat(defaultValues, Is.is(expectedDefaultValues));
		
		List<String> rewardNames = model.simpleRewardList.stream().map(reward -> reward.getConfigurationName()).collect(Collectors.toList());
		List<String> expectedRewardNames = Arrays.asList(new String[] {"Idle", "ServedRequests"});
		assertThat(rewardNames, Is.is(expectedRewardNames));
	}
	
	@Test
	public void fil3Test() {
		SpdnModel model = TestModel.FIL3.model();
			
		assertEquals(model.name, "DiningPhilosophers");
			
		List<String> paramNames = model.simpleParameterList.stream().map(param -> param.getName()).collect(Collectors.toList());
		List<String> expectedParamNames = Arrays.asList(new String[] {"phil3_eatingRate", "phil1_eatingRate", "phil2_eatingRate"});
		assertThat(paramNames, Is.is(expectedParamNames));
			
		List<Double> defaultValues = model.SpdnParameterList.stream().map(handler -> handler.defaultValue).collect(Collectors.toList());
		List<Double> expectedDefaultValues = Arrays.asList(new Double[] {9.680783503298795, 7.33569408796258, 2.156376928966199});
		assertThat(defaultValues, Is.is(expectedDefaultValues));
			
		List<String> rewardNames = model.simpleRewardList.stream().map(reward -> reward.getConfigurationName()).collect(Collectors.toList());
		List<String> expectedRewardNames = Arrays.asList(new String[] {"phil2_thinkingTime", "Table_totalThinkingTime", "phil1_thinkingTime", "phil3_thinkingTime"});
		assertThat(rewardNames, Is.is(expectedRewardNames));
	}

}
