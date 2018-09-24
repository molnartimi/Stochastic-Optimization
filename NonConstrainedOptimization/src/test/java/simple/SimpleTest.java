package simple;

import org.junit.Test;

import algorithms.BeesAlgorithm;
import algorithms.GradientDescent;
import algorithms.LBFGS;
import algorithms.ParticleSwarm;
import algorithms.ParticleSwarmWithGradientDescent;
import algorithms.SimulatedAnnealing;
import models.Model;
import spdn.SPDNResult;

public class SimpleTest {
	
	Model model = Model.SIMPLE_SERVER;
	
	public void printResult(SPDNResult result) {
		final String separator = "-----------------------------";
		System.out.println();
		System.out.println(separator + "\n" + result.toString() + "\n" + separator);
	}
	
	@Test
	public void lbfgs() {
		printResult(new LBFGS(model).optimize(0, 2, 0, new double[0], 0));
	}
	
	@Test
	public void gd() {
		printResult(new GradientDescent(model).optimize(1, 0, new double[0], 0));
	}
	
	@Test
	public void pso() {
		printResult(new ParticleSwarm(model).optimize(2, 1, 0, 0, 0));
	}
	
	@Test
	public void psgd() {
		printResult(new ParticleSwarmWithGradientDescent(model).optimize(2, 1, 0, 0, 0, 0, 0));
	}
	
	@Test
	public void bees() {
		printResult(new BeesAlgorithm(model).optimize(2, 0, 0, 5, 1, 1, 1, 1));
	}
	
	@Test
	public void sima() {
		printResult(new SimulatedAnnealing(model).optimize(0, 0, 0, 0, 0));
	}
}
