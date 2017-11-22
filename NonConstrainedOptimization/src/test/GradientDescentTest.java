package test;

import algorithms.GradientDescent;
import models.Models;

public class GradientDescentTest extends BaseTest{

	@Override
	protected void test(Models model) {
		GradientDescent opt = new GradientDescent(model);
		
		System.out.println(opt.optimize(0, 0, new double[0], 5).toString());
		System.out.println(opt.optimize(0.5, 0, new double[0], 10).toString());
		System.out.println(opt.optimize(2, 0, new double[0], 10).toString());
	}

}
