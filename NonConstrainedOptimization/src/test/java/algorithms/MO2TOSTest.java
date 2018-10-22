package algorithms;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import algorithms.mo2tos.MO2TOSHyperParam;
import algorithms.mo2tos.MO2TOSHyperParamBuilder;
import algorithms.mo2tos.MO2TOS_v0;
import algorithms.mo2tos.MO2TOS_v1;
import algorithms.mo2tos.dto.Group;
import algorithms.mo2tos.helper.AllocationHandler;
import model.TestModel;

public class MO2TOSTest extends BaseTest {
	
	@Test
	public void firstTest() {
		MO2TOSHyperParam params = new MO2TOSHyperParamBuilder()
				.groupNumber(5)
				.heighModelSampleNumPerIter(20)
				.lowModelSampleNum(1000)
				.build();
		optimize(new MO2TOS_v0(TestModel.FIL3.model()), params);
	}
	
	@Test
	public void secondTest() {
		MO2TOSHyperParam params = new MO2TOSHyperParamBuilder()
				.groupNumber(5)
				.heighModelSampleNumPerIter(20)
				.lowModelSampleNum(1000)
				.maxError(200)
				.build();
		optimize(new MO2TOS_v1(TestModel.FIL3.model()), params);
	}
	
	@Test
	public void optimalAllocationTest() {
		AllocationHandler allocHandler = new AllocationHandler(3, 30);
		List<Group> groups = new ArrayList<>();
		groups.add(testGroup(1, 1));
		groups.add(testGroup(2, 2));
		groups.add(testGroup(3, 3));
		
		allocHandler.recalcAllocations(groups);
		
		assertTrue(allocHandler.getGroupAlloc(0) == 8);
		assertTrue(allocHandler.getGroupAlloc(1) == 14);
		assertTrue(allocHandler.getGroupAlloc(2) == 8);
	}

	private Group testGroup(double mean, double variance) {
		try {
			Group g = new Group(null);
			Field meanField;
			meanField = Group.class.getDeclaredField("mean");
			meanField.setAccessible(true);
			meanField.set(g, mean);
			
			Field varianceField = Group.class.getDeclaredField("variance");
			varianceField.setAccessible(true);
			varianceField.set(g, variance);
			
			return g;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
