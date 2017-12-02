package test;

import org.junit.Ignore;
import org.junit.Test;

import algorithms.*;
import models.Model;
import spdn.SPDNResult;

public class AlgorithmTest {
	public static double epszilon=0.001;
	private Model model = Model.FIL3;
	
	@Test
	//@Ignore
	public void lbfgsTest(){
		LBFGS opt = new LBFGS(model);
		SPDNResult result = opt.optimize(0,0,0, new double[0], 5);
		System.out.println(result.toString());
		result.writeToCsv();
	}
	
	@Test
	//@Ignore
	public void gradientTest(){
		GradientDescent opt = new GradientDescent(model);
		SPDNResult result = opt.optimize(0,0,new double[0], 10);
		System.out.println(result.toString());
		result.writeToCsv();
	}
	
	@Test
	//@Ignore
	public void particleSwarmTest(){
		ParticleSwarm opt = new ParticleSwarm(model);
		SPDNResult result = opt.optimize(0,0,0,0,0);
		System.out.println(result.toString());
		result.writeToCsv();
	}
	
	@Test
	//@Ignore
	public void particleSwarmWithGradientDescentTest(){
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(model);
		SPDNResult result = opt.optimize(0,0,0,0,0,0,0);
		System.out.println(result.toString());
		result.writeToCsv();
	}
	
	@Test
	//@Ignore
	public void beesTest(){
		BeesAlgorithm opt = new BeesAlgorithm(model);
		SPDNResult result = opt.optimize(0,0,0,0,0,0,0,0);
		System.out.println(result.toString());
		result.writeToCsv();
	}
	
	@Test
	//@Ignore
	public void simulatedAnnealingTest(){
		SimulatedAnnealing opt = new SimulatedAnnealing(model);
		SPDNResult result = opt.optimize(100,0,0.1,0,5);
		System.out.println(result.toString());
		result.writeToCsv();
	}

	
}
