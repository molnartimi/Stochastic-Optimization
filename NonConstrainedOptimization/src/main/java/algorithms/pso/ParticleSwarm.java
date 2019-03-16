package algorithms.pso;import java.util.ArrayList;import java.util.List;import java.util.Random;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import algorithms.Optimizer;import algorithms.OptimizerResult;import algorithms.Sample;import algorithms.ToleranceExceededException;import model.Model;public class ParticleSwarm extends Optimizer<PSOHyperParam> {	public static final String ID = "PSOP";	protected static final Logger logger = LoggerFactory.getLogger(ParticleSwarm.class);			protected Sample<List<Double>> globalBestPosition;	protected double tolerance;		public ParticleSwarm(Model model) {		super(model);	}	protected class Particle {				private Sample<List<Double>> position, bestPosition;		private List<Double> velocity;		public Particle(List<Double> pos, List<Double> vel) {			velocity = vel;			double value = modelChecker.calcObjective(pos);			position = new Sample<>(pos, value);			bestPosition = position;		}		public Sample<List<Double>> getBestPosition() {			return bestPosition;		}		public List<Double> getVelocity() {			return velocity;		}		public Sample<List<Double>> getPosition() {			return position;		}		public void step(List<Double> newPos, List<Double> newVel) throws ToleranceExceededException {			model.cutParamsOnBorder(newPos);			double value = modelChecker.calcObjective(newPos);			position = new Sample<>(newPos, value);			velocity = newVel;			if (position.compareTo(bestPosition) < 0) {				bestPosition = position;			}		}	}	public OptimizerResult optimize(PSOHyperParam params) {		logger.info("Start to optimize " + model.id);		long startTime = System.nanoTime();		tolerance = params.tolerance;				logger.info("Initializing swarm");		try {			List<Particle> swarm = initSwarm(params.swarmSize);					logger.info("Starting to step particles");			for (int i = 0; i < params.maxIter; i++) {				for (int j = 0; j < params.swarmSize; j++) {					Particle p = swarm.get(j);					stepParticle(p, params.omega, params.fiParticle, params.fiGlobal);					if (p.getBestPosition().compareTo(globalBestPosition) < 0) {						updateBest(p.getBestPosition());					}				}			}		} catch (ToleranceExceededException e) {			logger.info("Tolerance border exceeded, stop optimization");		}		OptimizerResult result = new OptimizerResult(globalBestPosition.value, globalBestPosition.point, ID, params.getHyperParams(), model);		result.setTime(System.nanoTime() - startTime);		return result;	}		protected void stepParticle(Particle p, double omega, double fiParticle, double fiGlobal) throws ToleranceExceededException {		Random r = new Random();				List<Double> newPos = new ArrayList<>();		List<Double> newVel = new ArrayList<>();		for (int dim = 0; dim < model.parameterSize(); dim++) {			double posAttr = p.getPosition().point.get(dim);			double bestPosAttr = p.getBestPosition().point.get(dim);			double velAttr = p.getVelocity().get(dim);			double globalBestPosAttr = globalBestPosition.point.get(dim);						double newVelAttr = omega * velAttr +					fiParticle * r.nextDouble() * (bestPosAttr - posAttr) +					fiGlobal * r.nextDouble() * (globalBestPosAttr - posAttr);			newVel.add(newVelAttr);			newPos.add(posAttr + newVelAttr);		}		p.step(newPos, newVel);	}		protected List<Particle> initSwarm(int swarmSize) throws ToleranceExceededException {		List<Particle> swarm = new ArrayList<>();		List<List<Double>> randoms = model.latinHypercubeParamValues(swarmSize);		for (List<Double> paramValueList: randoms) {			Particle p = new Particle((List<Double>) paramValueList, model.randomVelocity());			swarm.add(p);			if (globalBestPosition == null || p.getPosition().compareTo(globalBestPosition) < 0) {				updateBest(p.getPosition());			}		}		return swarm;	}		private void updateBest(Sample<List<Double>> newBestPosition) throws ToleranceExceededException {		logger.info("New best value " + newBestPosition.value + " in point " + newBestPosition.point.toString());		globalBestPosition = newBestPosition;		if (globalBestPosition.value < tolerance) throw new ToleranceExceededException();	}	}