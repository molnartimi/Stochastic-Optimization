package test;

import algorithms.SimulatedAnnealing;
import models.Model;

public class SimulatedAnnealingTest extends BaseTest{

	@Override
	protected void test(Model model) {
		SimulatedAnnealing opt = new SimulatedAnnealing(model);
		
		System.out.println(opt.optimize(0, 0, 0, 0, 0).toString());
		System.out.println(opt.optimize(0, 0, 0, 0, 10).toString());
		System.out.println(opt.optimize(0, 0, 1, 0, 5).toString());
		System.out.println(opt.optimize(300, 0, 1, 0, 5).toString());
	}

}
