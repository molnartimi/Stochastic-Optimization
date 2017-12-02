package test;

import org.junit.Test;

import algorithms.ParticleSwarmWithGradientDescent;
import models.Model;

public class ParticleSwarmWithGradientDescentTest{
	/// swarm = 10, iter = 10
	@Test
	public void test1() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL3);
		System.out.println(opt.optimize(10, 10, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test2() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL5);
		System.out.println(opt.optimize(10, 10, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test3() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL7);
		System.out.println(opt.optimize(10, 10, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void tes4() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL9);
		System.out.println(opt.optimize(10, 10, 0, 0, 0, 0, 0).toString());
	}
		
	/// swarm = 30, iter = 10
	@Test
	public void test5() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL3);
		System.out.println(opt.optimize(30, 10, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test6() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL5);
		System.out.println(opt.optimize(30, 10, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test7() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL7);
		System.out.println(opt.optimize(30, 10, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void tes8() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL9);
		System.out.println(opt.optimize(30, 10, 0, 0, 0, 0, 0).toString());
	}
	
	/// swarm = 10, iter = 30
	@Test
	public void test9() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL3);
		System.out.println(opt.optimize(10, 30, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test10() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL5);
		System.out.println(opt.optimize(10, 30, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test11() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL7);
		System.out.println(opt.optimize(10, 30, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void tes12() {
		ParticleSwarmWithGradientDescent opt = new ParticleSwarmWithGradientDescent(Model.FIL9);
		System.out.println(opt.optimize(10, 30, 0, 0, 0, 0, 0).toString());
	}
}
