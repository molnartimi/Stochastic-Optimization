package algorithms.mo2tos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.Optimizer;
import algorithms.OptimizerResult;
import algorithms.mo2tos.dto.Group;
import algorithms.mo2tos.dto.Sample;
import algorithms.mo2tos.helper.AllocationHandler;
import model.Model;
import model.spdn.SpdnModel;

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
		
		SortedSet<Sample> OTSamples = ordinalTransform(params.lowModelSampleNum, params.maxError);
		List<Group> groups = separateToGroups(OTSamples, params.groupNum);
		logger.info("Ordinal transformation done");
		
		logger.info("Set model analyzer tolerance to " + params.heighModelTol);
		modelChecker.setTolerance(params.heighModelTol);
		Sample optimum = optimalSample(params.maxIter, groups, params.maxError);
		
		logger.info("Found optimum: " + optimum.getHeighResult() + " at point : " + optimum.values.toString());
		OptimizerResult result = new OptimizerResult(optimum.getHeighResult(), optimum.values, ID, params.getHyperParams(), model);
		result.setTime(System.nanoTime() - startTime);
		return result;
	}
	
	protected abstract SortedSet<Sample> ordinalTransform(int lowCalcNum, int maxError);

	protected abstract Sample optimalSample(int maxIter, List<Group> groups, int maxError);

	protected List<Group> separateToGroups(Set<Sample> OTSamples, int groupNum) {
		List<Group> groups = new ArrayList<>();
		int n = OTSamples.size() / groupNum;
		for (int i = 0; i < groupNum; i++) {
			List<Sample> subList = OTSamples.stream()
					.skip(i * n)
					.limit(n)
					.collect(Collectors.toList());
			groups.add(new Group(subList));
		}
		return groups;
	}

}
