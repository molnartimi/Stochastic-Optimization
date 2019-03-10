package algorithms.bees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.Optimizer;
import algorithms.OptimizerResult;
import algorithms.ToleranceExceededException;
import model.Model;

public class BeesAlgorithm extends Optimizer<BeesHyperParam> {
	public static final String ID = "BEES";
	protected static final Logger logger = LoggerFactory.getLogger(BeesAlgorithm.class);
	protected double tolerance;

	public BeesAlgorithm(Model model) {
		super(model);
	}

	private class Bee {
		private List<Double> pos;
		private double value;

		public Bee(List<Double> p, double val) {
			pos = p;
			value = val;
		}

		public void setPos(List<Double> p, double val) {
			model.cutParamsOnBorder(p);
			pos = p;
			value = val;
		}

		public List<Double> getPos() {
			return pos;
		}

		public double getVal() {
			return value;
		}
	}

	private class BeeComparator implements Comparator<Bee> {
		@Override
		public int compare(Bee b1, Bee b2) {
			double result = b1.getVal() - b2.getVal();
			if (result < 0)
				return -1;
			else if (result == 0)
				return 0;
			else
				return 1;
		}
	}

	@Override
	public OptimizerResult optimize(BeesHyperParam params) {
		logger.info("Start to optimize " + model.id);
		long startTime = System.nanoTime();
		tolerance = params.tolerance;
		
		logger.info("Initializing swarm");
		List<Bee> scouts = initSwarm(params.scoutSize);
		double radius = params.radius;
		
		logger.info("Starting to optimize");
		BeeComparator comparator = new BeeComparator();
		Collections.sort(scouts, comparator);

		try {
			for (int iter = 0; iter < params.maxIter; iter++) {
				logger.info("New iteration starting, actual best value " + scouts.get(0).getVal() + " in point " + scouts.get(0).getPos().toString());
				
				calculateLeadsAndRecruiteds(scouts, 0, params.eliteBeesSize, params.recruitedOfElitesSize, radius);
				calculateLeadsAndRecruiteds(scouts, params.eliteBeesSize, params.bestBeesSize, params.recruitedOfBestsSize,
						radius);
				randomizeOtherScouts(scouts, params.eliteBeesSize + params.bestBeesSize);
	
				Collections.sort(scouts, comparator);
	
				radius *= params.radiusSmallerRate;
			}
		} catch (ToleranceExceededException e) {
			logger.info("Tolerance border exceeded, stop optimization");
		}

		OptimizerResult result = new OptimizerResult(scouts.get(0).getVal(), scouts.get(0).getPos(), ID, params.getHyperParams(), model);

		result.setTime(System.nanoTime() - startTime);
		return result;
	}

	private void calculateLeadsAndRecruiteds(List<Bee> scouts, int fromIdx, int count, int recruitedSize, double radius) throws ToleranceExceededException {
		Random r = new Random();

		for (int i = fromIdx; i < fromIdx + count; i++) {
			Bee lead = scouts.get(i);

			for (int j = 0; j < recruitedSize; j++) {
				List<Double> pos = new ArrayList<>();

				for (int d = 0; d < model.parameterSize(); d++) {
					pos.add(lead.getPos().get(d) + (r.nextDouble() * 2 - 1) * radius);
				}

				model.cutParamsOnBorder(pos);
				double newPosValue = modelChecker.calcObjective(pos);
				if (newPosValue < lead.getVal()) {
					lead.setPos(pos, newPosValue);
					if (newPosValue < tolerance) throw new ToleranceExceededException();
				}
			}
		}
	}

	private void randomizeOtherScouts(List<Bee> scouts, int fromIdx) {
		for (int i = fromIdx; i < scouts.size(); i++) {
			List<Double> pos = model.randomParamValues();
			scouts.get(i).setPos(pos, modelChecker.calcObjective(pos));
		}
	}

	private List<Bee> initSwarm(int scoutSize) {
		ArrayList<Bee> swarm = new ArrayList<>();

		model.latinHypercubeParamValues(scoutSize).forEach(paramValue -> swarm
				.add(new Bee((List<Double>) paramValue, modelChecker.calcObjective((List<Double>) paramValue))));
		return swarm;
	}

}
