package test;

import org.junit.Test;

import algorithms.GradientDescent;
import models.Model;

public class GradientDescentTest{
	/// gamma = 1, restart = 5
	@Test
	public void test1() {
		GradientDescent opt = new GradientDescent(Model.FIL3);
		System.out.println(opt.optimize(1, 0, new double[0], 5).toString());
	}
	@Test
	public void test2() {
		GradientDescent opt = new GradientDescent(Model.FIL5);
		System.out.println(opt.optimize(1, 0, new double[0], 5).toString());
	}
	@Test
	public void test3() {
		GradientDescent opt = new GradientDescent(Model.FIL7);
		System.out.println(opt.optimize(1, 0, new double[0], 5).toString());
	}
	@Test
	public void tes4() {
		GradientDescent opt = new GradientDescent(Model.FIL9);
		System.out.println(opt.optimize(1, 0, new double[0], 5).toString());
	}
		
	/// gamma = 0.5, restart = 5
	@Test
	public void test5() {
		GradientDescent opt = new GradientDescent(Model.FIL3);
		System.out.println(opt.optimize(0.5, 0, new double[0], 5).toString());
	}
	@Test
	public void test6() {
		GradientDescent opt = new GradientDescent(Model.FIL5);
		System.out.println(opt.optimize(0.5, 0, new double[0], 5).toString());
	}
	@Test
	public void test7() {
		GradientDescent opt = new GradientDescent(Model.FIL7);
		System.out.println(opt.optimize(0.5, 0, new double[0], 5).toString());
	}
	@Test
	public void test8() {
		GradientDescent opt = new GradientDescent(Model.FIL9);
		System.out.println(opt.optimize(0.5, 0, new double[0], 5).toString());
	}
			
	/// gamma = 1, restart = 15
	@Test
	public void test9() {
		GradientDescent opt = new GradientDescent(Model.FIL3);
		System.out.println(opt.optimize(1, 0, new double[0], 15).toString());
	}
	@Test
	public void test10() {
		GradientDescent opt = new GradientDescent(Model.FIL5);
		System.out.println(opt.optimize(1, 0, new double[0], 15).toString());
	}
	@Test
	public void test11() {
		GradientDescent opt = new GradientDescent(Model.FIL7);
		System.out.println(opt.optimize(1, 0, new double[0], 15).toString());
	}
	@Test
	public void tes12() {
		GradientDescent opt = new GradientDescent(Model.FIL9);
		System.out.println(opt.optimize(1, 0, new double[0], 15).toString());
	}			

}
