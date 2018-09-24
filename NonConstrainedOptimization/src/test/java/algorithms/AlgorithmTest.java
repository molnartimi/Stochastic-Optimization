package algorithms;

import org.junit.Test;

import models.Model;
import spdn.SPDNResult;

public class AlgorithmTest {
	public static double epszilon=0.001;
	private Model model1 = Model.SIMPLE_SERVER;
	private Model model2 = Model.VCL_STOCHASTIC;
	private Model model3 = Model.HYBRID_CLOUD;
	
	@Test
	//@Ignore
	public void lbfgsTest1(){
		LBFGS opt = new LBFGS(model1);
		SPDNResult result = opt.optimize(0,30,0, new double[0], 5);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void lbfgsTest2(){
		LBFGS opt = new LBFGS(model2);
		SPDNResult result = opt.optimize(0,30,0, new double[0], 5);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void lbfgsTest3(){
		LBFGS opt = new LBFGS(model3);
		SPDNResult result = opt.optimize(0,30,0, new double[0], 5);
		System.out.println(result.toString());
	}
	
	@Test
	//@Ignore
	public void gradientTest1(){
		GradientDescent opt = new GradientDescent(model1);
		SPDNResult result = opt.optimize(0,0,new double[0], 15);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void gradientTest2(){
		GradientDescent opt = new GradientDescent(model2);
		SPDNResult result = opt.optimize(0,0,new double[0], 15);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void gradientTest3(){
		GradientDescent opt = new GradientDescent(model3);
		SPDNResult result = opt.optimize(0,0,new double[0], 15);
		System.out.println(result.toString());
	}
	
	@Test
	//@Ignore
	public void particleSwarmTest1(){
		ParticleSwarm opt = new ParticleSwarm(model1);
		SPDNResult result = opt.optimize(30,10,0,0,0);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void particleSwarmTest2(){
		ParticleSwarm opt = new ParticleSwarm(model2);
		SPDNResult result = opt.optimize(30,10,0,0,0);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void particleSwarmTest3(){
		ParticleSwarm opt = new ParticleSwarm(model3);
		SPDNResult result = opt.optimize(30,10,0,0,0);
		System.out.println(result.toString());
	}
	
	@Test
	//@Ignore
	public void particleSwarmWithGradientDescentTest1(){
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(model1);
		SPDNResult result = opt.optimize(30,10,15,0,0,0,0);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void particleSwarmWithGradientDescentTest2(){
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(model2);
		SPDNResult result = opt.optimize(30,10,15,0,0,0,0);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void particleSwarmWithGradientDescentTest3(){
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(model3);
		SPDNResult result = opt.optimize(30,10,15,0,0,0,0);
		System.out.println(result.toString());
	}
	
	@Test
	//@Ignore
	public void beesTest1(){
		BeesAlgorithm opt = new BeesAlgorithm(model1);
		SPDNResult result = opt.optimize(5,0,0,20,10,8,3,5);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void beesTest2(){
		BeesAlgorithm opt = new BeesAlgorithm(model2);
		SPDNResult result = opt.optimize(5,0,0,20,10,8,3,5);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void beesTest3(){
		BeesAlgorithm opt = new BeesAlgorithm(model3);
		SPDNResult result = opt.optimize(5,0,0,20,10,8,3,5);
		System.out.println(result.toString());
	}
	
	@Test
	//@Ignore
	public void simulatedAnnealingTest1(){
		SimulatedAnnealing opt = new SimulatedAnnealing(model1);
		SPDNResult result = opt.optimize(100,0,0.3,0,5);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void simulatedAnnealingTest2(){
		SimulatedAnnealing opt = new SimulatedAnnealing(model2);
		SPDNResult result = opt.optimize(100,0,0.3,0,5);
		System.out.println(result.toString());
	}
	@Test
	//@Ignore
	public void simulatedAnnealingTest3(){
		SimulatedAnnealing opt = new SimulatedAnnealing(model3);
		SPDNResult result = opt.optimize(100,0,0.3,0,5);
		System.out.println(result.toString());
	}

	
}
