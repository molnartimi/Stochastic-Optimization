package algorithms.msc.mo2tos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import algorithms.Optimizer;
import models.Model;
import spdn.SPDNResult;

public abstract class MO2TOS extends Optimizer<MO2TOSHyperParam>{
	public static final String ID = "MO2TOS";
	protected AllocationHandler allocationHandler;
	
	public MO2TOS(Model model) {
		super(model);
	}

	@Override
	public SPDNResult optimize(MO2TOSHyperParam params) {
		allocationHandler = new AllocationHandler(params.groupNum, params.heighModelSampleNumPerIter);
		spdn.setTolerance(params.lowModelTol);
		
		SortedSet<Sample> OTSamples = ordinalTransform(params.lowModelSampleNum, params.maxError);
		List<Group> groups = separateToGroups(OTSamples, params.groupNum);
		
		spdn.setTolerance(params.heighModelTol);
		Sample optimum = optimalSample(params.maxIter, groups, params.maxError);
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
