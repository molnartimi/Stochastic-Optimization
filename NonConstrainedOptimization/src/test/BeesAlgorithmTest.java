package test;

import algorithms.BeesAlgorithm;
import models.Model;

public class BeesAlgorithmTest extends BaseTest{

	@Override
	protected void test(Model model) {
		BeesAlgorithm opt = new BeesAlgorithm(model);
		
		System.out.println(opt.optimize(0, 0, 0, 0, 0, 0, 0, 0).toString());
		System.out.println(opt.optimize(50, 0, 0, 0, 0, 0, 0, 0).toString());
		System.out.println(opt.optimize(10, 1, 0, 0, 0, 0, 0, 0).toString());
		System.out.println(opt.optimize(0, 0, 0, 50, 20, 10, 0, 0).toString());
		System.out.println(opt.optimize(0, 0, 0, 0, 0, 0, 10, 20).toString());
	}

}
