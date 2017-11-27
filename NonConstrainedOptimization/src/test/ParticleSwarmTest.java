package test;

import algorithms.ParticleSwarm;
import models.Model;

public class ParticleSwarmTest extends BaseTest{

	@Override
	protected void test(Model model) {
		ParticleSwarm opt = new ParticleSwarm(model);
		
		System.out.println(opt.optimize(0, 0, 0, 0, 0).toString());
		System.out.println(opt.optimize(0, 50, 0, 0, 0).toString());
		System.out.println(opt.optimize(50, 0, 0, 0, 0).toString());
		System.out.println(opt.optimize(100, 0, 0, 0, 0).toString());
		System.out.println(opt.optimize(0, 50, 0, 0.8, 0.2).toString());
		System.out.println(opt.optimize(0, 50, 0, 0.2, 0.8).toString());
		System.out.println(opt.optimize(50, 50, 0.5, 0.5, 0.5).toString());
	}

}
