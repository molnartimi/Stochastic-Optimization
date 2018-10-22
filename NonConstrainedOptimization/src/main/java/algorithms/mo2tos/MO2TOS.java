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
import algorithms.mo2tos.dto.Group;
import algorithms.mo2tos.dto.Sample;
import algorithms.mo2tos.helper.AllocationHandler;
import spdn.SPDNResult;
import spdn.model.SpdnModel;

public abstract class MO2TOS extends Optimizer<MO2TOSHyperParam>{
	public static final String ID = "MO2TOS";
	protected AllocationHandler allocationHandler;
	protected static final Logger logger = LoggerFactory.getLogger(MO2TOS.class);
	
	public MO2TOS(SpdnModel model) {
		super(model);
	}

	@Override
	public SPDNResult optimize(MO2TOSHyperParam params) {
		logger.info("Start to optimize " + model.id);
		allocationHandler = new AllocationHandler(params.groupNum, params.heighModelSampleNumPerIter);
		logger.info("Set model analyzer tolerance to " + params.lowModelTol);
		spdn.setTolerance(params.lowModelTol);
		
		SortedSet<Sample> OTSamples = ordinalTransform(params.lowModelSampleNum, params.maxError);
		List<Group> groups = separateToGroups(OTSamples, params.groupNum);
		logger.info("Ordinal transformation done");
		
		logger.info("Set model analyzer tolerance to " + params.heighModelTol);
		spdn.setTolerance(params.heighModelTol);
		Sample optimum = optimalSample(params.maxIter, groups, params.maxError);
		logger.info("Found optimum: " + optimum.getHeighResult() + " at point : " + optimum.values.toString());
		return new SPDNResult(optimum.getHeighResult(), optimum.values, ID, params.getHyperParams(), model);
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
