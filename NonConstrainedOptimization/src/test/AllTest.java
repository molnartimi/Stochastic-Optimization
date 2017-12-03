package test;

import algorithms.BeesAlgorithm;
import algorithms.GradientDescent;
import algorithms.LBFGS;
import algorithms.ParticleSwarm;
import algorithms.ParticleSwarmWithGradientDescent;
import algorithms.SimulatedAnnealing;
import models.Model;

public class AllTest {

	public static void main(String[] args) {
		Model[] models = new Model[] {Model.HYBRID_CLOUD};
		for(Model m: models) {
			//lbfgs(m);
			//gd(m);
			//pso(m);
			//psgd(m);
			//bees(m);
			sima(m);
		}
		

	}

	private static void lbfgs(Model m) {
		System.out.println(new LBFGS(m).optimize(0, 10, 0, new double[0], 10).toString());
		System.out.println(new LBFGS(m).optimize(0, 30, 0, new double[0], 0).toString());
		System.out.println(new LBFGS(m).optimize(0, 30, 0, new double[0], 10).toString());
		System.out.println(new LBFGS(m).optimize(10, 10, 0, new double[0], 10).toString());
	}
	
	private static void gd(Model m) {
		System.out.println(new GradientDescent(m).optimize(1, 0, new double[0], 10).toString());
		System.out.println(new GradientDescent(m).optimize(0.5, 0, new double[0], 10).toString());
		System.out.println(new GradientDescent(m).optimize(1, 0, new double[0], 30).toString());
	}
	
	private static void pso(Model m) {
		System.out.println(new ParticleSwarm(m).optimize(10, 10, 0, 0, 0).toString());
		System.out.println(new ParticleSwarm(m).optimize(30, 10, 0, 0, 0).toString());
		System.out.println(new ParticleSwarm(m).optimize(10, 30, 0, 0, 0).toString());
		System.out.println(new ParticleSwarm(m).optimize(10, 10, 0, 0.6, 0.4).toString());
	}
	
	private static void psgd(Model m) {
		System.out.println(new ParticleSwarmWithGradientDescent(m).optimize(10, 10, 0, 0, 0, 0, 0).toString());
		System.out.println(new ParticleSwarmWithGradientDescent(m).optimize(30, 10, 0, 0, 0, 0, 0).toString());
		System.out.println(new ParticleSwarmWithGradientDescent(m).optimize(10, 30, 0, 0, 0, 0, 0).toString());
		System.out.println(new ParticleSwarmWithGradientDescent(m).optimize(10, 10, 0, 0, 0, 0.6, 0.4).toString());
	}
	
	private static void bees(Model m) {
		System.out.println(new BeesAlgorithm(m).optimize(10, 0, 0, 10, 5, 2, 2, 5).toString());
		System.out.println(new BeesAlgorithm(m).optimize(5, 0, 0, 30, 5, 2, 2, 5).toString());
		System.out.println(new BeesAlgorithm(m).optimize(30, 0, 0, 10, 5, 2, 2, 5).toString());
	}
	
	private static void sima(Model m) {
		System.out.println(new SimulatedAnnealing(m).optimize(0, 0, 0, 0, 5).toString());
		System.out.println(new SimulatedAnnealing(m).optimize(300, 0, 0, 0, 2).toString());
		System.out.println(new SimulatedAnnealing(m).optimize(0, 0, 1, 0, 5).toString());
	}
}
