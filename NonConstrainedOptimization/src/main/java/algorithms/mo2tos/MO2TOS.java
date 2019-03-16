package algorithms.mo2tos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.Optimizer;
import algorithms.OptimizerResult;
import algorithms.mo2tos.dto.Group;
import algorithms.mo2tos.dto.MultiFidelitySample;
import algorithms.mo2tos.helper.AllocationHandler;
import model.Model;

public abstract class MO2TOS extends Optimizer<MO2TOSHyperParam>{
	public static final String ID = "MO2TOS";
	protected AllocationHandler allocationHandler;
	protected static final Logger logger = LoggerFactory.getLogger(MO2TOS.class);
	
	public MO2TOS(Model model) {
		super(model);
	}

	@Override
	public OptimizerResult optimize(MO2TOSHyperParam params) {
		logger.info("Start to optimize " + model.id);
		long startTime = System.nanoTime();
		allocationHandler = new AllocationHandler(params.groupNum, params.heighModelSampleNumPerIter);
		
		logger.info("Set model analyzer tolerance to " + params.lowModelTol);
		modelChecker.setTolerance(params.lowModelTol);
		
		SortedSet<MultiFidelitySample> OTSamples = ordinalTransform(params.lowModelSampleNum, params.maxError);
		List<Group> groups = separateToGroups(OTSamples, params.groupNum);
		logger.info("Ordinal transformation done");
		
		logger.info("Set model analyzer tolerance to " + params.heighModelTol);
		modelChecker.setTolerance(params.heighModelTol);
		MultiFidelitySample optimum = optimalSample(params.maxIter, groups, params.maxError);
		
		logger.info("Found optimum: " + optimum.getHeighResult() + " at point : " + optimum.point.toString());
		OptimizerResult result = new OptimizerResult(optimum.getHeighResult(), optimum.point, ID, params.getHyperParams(), model);
		result.setTime(System.nanoTime() - startTime);
		return result;
	}
	
	protected abstract SortedSet<MultiFidelitySample> ordinalTransform(int lowCalcNum, int maxError);

	protected abstract MultiFidelitySample optimalSample(int maxIter, List<Group> groups, int maxError);

	protected List<Group> separateToGroups(Set<MultiFidelitySample> OTSamples, int groupNum) {
		List<Group> groups = new ArrayList<>();
		int n = OTSamples.size() / groupNum;
		for (int i = 0; i < groupNum; i++) {
			List<MultiFidelitySample> subList = OTSamples.stream()
					.skip(i * n)
					.limit(n)
					.collect(Collectors.toList());
			groups.add(new Group(subList));
		}
		return groups;
	}

}
