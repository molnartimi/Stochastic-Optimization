package test;

import org.apache.commons.math3.linear.RealVector;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import algorithms.*;
import models.Models;
import spdn.SPDN;

public class AlgorithmTest {
	public static double epszilon=0.001;
	private SPDN f = new SPDN(Models.SIMPLE_SERVER,0);
	private Models model = Models.SIMPLE_SERVER;
	RealVector result;
	
	@Test
	//@Ignore
	public void lbfgsTest(){
		LBFGS opt = new LBFGS(model);
			
		result = opt.optimize(0,0,0, new double[0], 5);
		System.out.println("L-BFGS:");
	}
	
	@Test
	@Ignore
	public void gradientTest(){
		GradientDescent opt = new GradientDescent(model);
		
		result = opt.optimize(0,0,new double[0], 10);
		System.out.println("Gradient descent:");
	}
	
	@Test
	@Ignore
	public void particleSwarmTest(){
		ParticleSwarm opt = new ParticleSwarm(model);
		
		result = opt.optimize(0,0,0,0,0);
		System.out.println("Particle swarm optimalization:");
	}
	
	@Test
	@Ignore
	public void particleSwarmWithGradientDescentTest(){
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(model);
		
		result = opt.optimize(0,0,0,0,0);
		System.out.println("Particle swarm with gradient descent optimalization:");
	}
	
	@Test
	@Ignore
	public void beesTest(){
		BeesAlgorithm opt = new BeesAlgorithm(model);
		
		result = opt.optimize(0,0,0,0,0,0,0,0);
		System.out.println("Bees algorithm:");
	}
	
	@Test
	@Ignore
	public void simulatedAnnealingTest(){
		SimulatedAnnealing opt = new SimulatedAnnealing(model);
		
		result = opt.optimize(0,0,0,0,0,0);
		System.out.println("Simulated annealing:");
	}
	
	@After
	public void writeOutResult(){
		System.out.println("- Point:" + result.toString() + "\n- Value: " + f.f(result));
	}
	
}
