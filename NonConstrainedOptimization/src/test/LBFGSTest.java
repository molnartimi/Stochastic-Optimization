package test;

import algorithms.LBFGS;
import models.Models;

public class LBFGSTest extends BaseTest{

	@Override
	protected void test(Models model) {
		LBFGS opt = new LBFGS(model);
		
		System.out.println(opt.optimize(0, 0, 0, new double[0], 5).toString());
		System.out.println(opt.optimize(0, 50, 0, new double[0], 0).toString());
		System.out.println(opt.optimize(0, 50, 0, new double[0], 5).toString());
		System.out.println(opt.optimize(0, 100, 0, new double[0], 5).toString());
	}
	
}
