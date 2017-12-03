package test;

import org.junit.Test;

import algorithms.ParticleSwarm;
import models.Model;

public class ParticleSwarmTest{
	/// swarm = 10, iter = 10
	@Test
	public void test1() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL3);
		System.out.println(opt.optimize(10, 10, 0, 0, 0).toString());
	}
	@Test
	public void test2() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL5);
		System.out.println(opt.optimize(10, 10, 0, 0, 0).toString());
	}
	@Test
	public void test3() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL7);
		System.out.println(opt.optimize(10, 10, 0, 0, 0).toString());
	}
	@Test
	public void tes4() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL9);
		System.out.println(opt.optimize(10, 10, 0, 0, 0).toString());
	}
/*		
	/// swarm = 30, iter = 10
	@Test
	public void test5() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL3);
		System.out.println(opt.optimize(30, 10, 0, 0, 0).toString());
	}
	@Test
	public void test6() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL5);
		System.out.println(opt.optimize(30, 10, 0, 0, 0).toString());
	}
	@Test
	public void test7() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL7);
		System.out.println(opt.optimize(30, 10, 0, 0, 0).toString());
	}
	@Test
	public void tes8() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL9);
		System.out.println(opt.optimize(30, 10, 0, 0, 0).toString());
	}
	
	/// swarm = 10, iter = 30
	@Test
	public void test9() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL3);
		System.out.println(opt.optimize(10, 30, 0, 0, 0).toString());
	}
	@Test
	public void test10() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL5);
		System.out.println(opt.optimize(10, 30, 0, 0, 0).toString());
	}
	@Test
	public void test11() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL7);
		System.out.println(opt.optimize(10, 30, 0, 0, 0).toString());
	}
	@Test
	public void tes12() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL9);
		System.out.println(opt.optimize(10, 30, 0, 0, 0).toString());
	}
	
	/// fiParticle = 0.6, fiGlobal = 0.4
	@Test
	public void test13() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL3);
		System.out.println(opt.optimize(10, 10, 0, 0.6, 0.4).toString());
	}
	@Test
	public void test14() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL5);
		System.out.println(opt.optimize(10, 10, 0, 0.6, 0.4).toString());
	}
	@Test
	public void test15() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL7);
		System.out.println(opt.optimize(10, 10, 0, 0.6, 0.4).toString());
	}
	@Test
	public void tes16() {
		ParticleSwarm opt = new ParticleSwarm(Model.FIL9);
		System.out.println(opt.optimize(10, 10, 0, 0.6, 0.4).toString());
	}
*/
}
