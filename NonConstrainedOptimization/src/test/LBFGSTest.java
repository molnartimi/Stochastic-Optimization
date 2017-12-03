package test;

import algorithms.LBFGS;
import org.junit.Test;
import models.Model;

public class LBFGSTest{
	/// Max iter = 10, restart = 5
	@Test
	public void test1() {
		LBFGS opt = new LBFGS(Model.FIL3);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 5).toString());
	}
	@Test
	public void test2() {
		LBFGS opt = new LBFGS(Model.FIL5);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 5).toString());
	}
	@Test
	public void test3() {
		LBFGS opt = new LBFGS(Model.FIL7);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 5).toString());
	}
	@Test
	public void tes4() {
		LBFGS opt = new LBFGS(Model.FIL9);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 5).toString());
	}
	/*
	/// Max iter = 30, restart = 5
	@Test
	public void test5() {
		LBFGS opt = new LBFGS(Model.FIL3);
		System.out.println(opt.optimize(0, 30, 0, new double[0], 5).toString());
	}
	@Test
	public void test6() {
		LBFGS opt = new LBFGS(Model.FIL5);
		System.out.println(opt.optimize(0, 30, 0, new double[0], 5).toString());
	}
	@Test
	public void test7() {
		LBFGS opt = new LBFGS(Model.FIL7);
		System.out.println(opt.optimize(0, 30, 0, new double[0], 5).toString());
	}
	@Test
	public void tes8() {
		LBFGS opt = new LBFGS(Model.FIL9);
		System.out.println(opt.optimize(0, 30, 0, new double[0], 5).toString());
	}
	
	/// Max iter = 10, restart = 15
	@Test
	public void test9() {
		LBFGS opt = new LBFGS(Model.FIL3);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 15).toString());
	}
	@Test
	public void test10() {
		LBFGS opt = new LBFGS(Model.FIL5);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 15).toString());
	}
	@Test
	public void test11() {
		LBFGS opt = new LBFGS(Model.FIL7);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 15).toString());
	}
	@Test
	public void tes12() {
		LBFGS opt = new LBFGS(Model.FIL9);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 15).toString());
	}
	
	/// m = 10
	@Test
	public void test13() {
		LBFGS opt = new LBFGS(Model.FIL3);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 5).toString());
	}
	@Test
	public void test14() {
		LBFGS opt = new LBFGS(Model.FIL5);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 5).toString());
	}
	@Test
	public void test15() {
		LBFGS opt = new LBFGS(Model.FIL7);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 5).toString());
	}
	@Test
	public void tes16() {
		LBFGS opt = new LBFGS(Model.FIL9);
		System.out.println(opt.optimize(0, 10, 0, new double[0], 5).toString());
	}
	*/
}
