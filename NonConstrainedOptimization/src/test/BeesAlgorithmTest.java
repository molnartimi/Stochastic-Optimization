package test;

import org.junit.Test;

import algorithms.BeesAlgorithm;
import models.Model;

public class BeesAlgorithmTest{
	/// Max iter = 10
	@Test
	public void test1() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL3);
		System.out.println(opt.optimize(10, 0, 0, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test2() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL5);
		System.out.println(opt.optimize(10, 0, 0, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test3() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL7);
		System.out.println(opt.optimize(10, 0, 0, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void tes4() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL9);
		System.out.println(opt.optimize(10, 0, 0, 0, 0, 0, 0, 0).toString());
	}
	
	/// Max iter = 30
	@Test
	public void test5() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL3);
		System.out.println(opt.optimize(30, 0, 0, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test6() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL5);
		System.out.println(opt.optimize(30, 0, 0, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void test7() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL7);
		System.out.println(opt.optimize(30, 0, 0, 0, 0, 0, 0, 0).toString());
	}
	@Test
	public void tes8() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL9);
		System.out.println(opt.optimize(30, 0, 0, 0, 0, 0, 0, 0).toString());
	}
	
	/// More elit and best
	@Test
	public void test9() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL3);
		System.out.println(opt.optimize(10, 0, 0, 0, 10, 8, 0, 0).toString());
	}
	@Test
	public void test10() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL5);
		System.out.println(opt.optimize(10, 0, 0, 0, 10, 8, 0, 0).toString());
	}
	@Test
	public void test11() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL7);
		System.out.println(opt.optimize(10, 0, 0, 0, 10, 8, 0, 0).toString());
	}
	@Test
	public void tes12() {
		BeesAlgorithm opt = new BeesAlgorithm(Model.FIL9);
		System.out.println(opt.optimize(10, 0, 0, 0, 10, 8, 0, 0).toString());
	}

}
