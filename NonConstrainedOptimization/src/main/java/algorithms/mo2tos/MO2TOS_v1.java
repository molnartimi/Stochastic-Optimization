package algorithms.mo2tos;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import algorithms.mo2tos.dto.Sample;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import spdn.model.SpdnModel;

public class MO2TOS_v1 extends MO2TOS_v0 {

	public MO2TOS_v1(SpdnModel model) {
		super(model);
	}
	
	@Override
	protected SortedSet<Sample> ordinalTransform(int lowModelSampleNum, int maxError) {
		SortedSet<Sample> ordinalSpace = new TreeSet<>();
		int errorsInARow = 0;
		
		List<List<Double>> randomPoints = model.latinHypercubeParamValues(lowModelSampleNum);
		for (List<Double> point: randomPoints) {
			try {
				double result = spdn.calcObjective(point);
				ordinalSpace.add(new Sample(point, result));
			} catch (SpdnException e) {
				if (++errorsInARow >= maxError) {
					throw new SpdnException("Spdn failed " + errorsInARow + " times in a row, stopped trying.");
				}
			}
		}
		
		return ordinalSpace;
	}

}
