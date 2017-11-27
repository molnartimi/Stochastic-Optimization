package test;

import org.apache.commons.math3.linear.RealVector;
import org.junit.Ignore;
import org.junit.Test;

import algorithms.*;
import models.Model;

public class AlgorithmTest {
	public static double epszilon=0.001;
	private Model model = Model.SIMPLE_SERVER;
	RealVector result;
	
	@Test
	@Ignore
	public void lbfgsTest(){
		LBFGS opt = new LBFGS(model);
			
		System.out.println("L-BFGS:\n" + opt.optimize(0,0,0, new double[0], 5).toString());
	}
	
	@Test
	@Ignore
	public void gradientTest(){
		GradientDescent opt = new GradientDescent(model);
		
		System.out.println("Gradient descent:\n" + opt.optimize(0,0,new double[0], 10).toString());
	}
	
	@Test
	@Ignore
	public void particleSwarmTest(){
		ParticleSwarm opt = new ParticleSwarm(model);
		
		System.out.println("Particle swarm optimalization:\n" + opt.optimize(0,0,0,0,0).toString());
	}
	
	@Test
	@Ignore
	public void particleSwarmWithGradientDescentTest(){
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(model);
		
		System.out.println("Particle swarm with gradient descent optimalization:\n" + opt.optimize(0,0,0,0,0).toString());
	}
	
	@Test
	@Ignore
	public void beesTest(){
		BeesAlgorithm opt = new BeesAlgorithm(model);
		
		System.out.println("Bees algorithm:\n" + opt.optimize(0,0,0,0,0,0,0,0).toString());
	}
	
	@Test
	//@Ignore
	public void simulatedAnnealingTest(){
		SimulatedAnnealing opt = new SimulatedAnnealing(model);
		
		System.out.println("Simulated annealing:\n" + opt.optimize(0,0,0,0,0).toString());
	}
	
}
