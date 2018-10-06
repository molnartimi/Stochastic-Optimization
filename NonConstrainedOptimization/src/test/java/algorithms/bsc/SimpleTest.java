package algorithms.bsc;

import org.junit.Test;

import algorithms.BaseTest;
import algorithms.bsc.bees.BeesAlgorithm;
import algorithms.bsc.bees.BeesHyperParam;
import algorithms.bsc.bees.BeesHyperParamBuilder;
import algorithms.bsc.gd.GDHyperParam;
import algorithms.bsc.gd.GDHyperParamBuilder;
import algorithms.bsc.gd.GradientDescent;
import algorithms.bsc.lbfgs.LBFGS;
import algorithms.bsc.lbfgs.LBFGSHyperParam;
import algorithms.bsc.lbfgs.LBFGSHyperParamBuilder;
import algorithms.bsc.psgd.PSGDHyperParam;
import algorithms.bsc.psgd.PSGDHyperParamBuilder;
import algorithms.bsc.psgd.ParticleSwarmWithGradientDescent;
import algorithms.bsc.pso.PSOHyperParam;
import algorithms.bsc.pso.PSOHyperParamBuilder;
import algorithms.bsc.pso.ParticleSwarm;
import algorithms.bsc.simann.SimAnnHyperParam;
import algorithms.bsc.simann.SimAnnHyperParamBuilder;
import algorithms.bsc.simann.SimulatedAnnealing;

public class SimpleTest extends BaseTest{
	
	@Test
	public void lbfgs() {
		LBFGSHyperParam params = new LBFGSHyperParamBuilder(model)
				.maxIter(2)
				.build();
		optimize(new LBFGS(model), params);
	}
	
	@Test
	public void gd() {
		GDHyperParam params = new GDHyperParamBuilder(model)
				.build();
		optimize(new GradientDescent(model), params);
	}
	
	@Test
	public void pso() {
		PSOHyperParam params = new PSOHyperParamBuilder()
				.swarmSize(2)
				.maxIter(1)
				.build();
		optimize(new ParticleSwarm(model), params);
	}
	
	@Test
	public void psgd() {
		PSGDHyperParam params = new PSGDHyperParamBuilder()
				.swarmSize(2)
				.maxIter(1)
				.gradientMaxIter(1)
				.build();
		optimize(new ParticleSwarmWithGradientDescent(model), params);
	}
	
	@Test
	public void bees() {
		BeesHyperParam params = new BeesHyperParamBuilder()
				.scoutSize(3)
				.bestBeesSize(1)
				.eliteBeesSize(1)
				.maxIter(1)
				.recruitedOfBestsSize(1)
				.recruitedOfElitesSize(1)
				.build();
		optimize(new BeesAlgorithm(model), params);
	}
	
	@Test
	public void sima() {
		SimAnnHyperParam params = new SimAnnHyperParamBuilder()
				.initTemp(5)
				.innerRestart(1)
				.build();
		optimize(new SimulatedAnnealing(model), params);
	}
}
