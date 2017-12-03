package test;

import org.junit.Test;

import algorithms.SimulatedAnnealing;
import models.Model;

public class SimulatedAnnealingTest{
	/// restart = 5
	@Test
	public void test1() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL3);
		System.out.println(opt.optimize(0, 0, 0, 0, 5).toString());
	}
	@Test
	public void test2() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL5);
		System.out.println(opt.optimize(0, 0, 0, 0, 5).toString());
	}
	@Test
	public void test3() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL7);
		System.out.println(opt.optimize(0, 0, 0, 0, 5).toString());
	}
/*	@Test
	public void tes4() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL9);
		System.out.println(opt.optimize(0, 0, 0, 0, 5).toString());
	}

	/// init temperature = 300
	@Test
	public void test5() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL3);
		System.out.println(opt.optimize(300, 0, 0, 0, 5).toString());
	}
	@Test
	public void test6() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL5);
		System.out.println(opt.optimize(300, 0, 0, 0, 5).toString());
	}
	@Test
	public void test7() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL7);
		System.out.println(opt.optimize(300, 0, 0, 0, 5).toString());
	}
	@Test
	public void tes8() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL9);
		System.out.println(opt.optimize(300, 0, 0, 0, 5).toString());
	}

	/// init border = 1
	@Test
	public void test9() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL3);
		System.out.println(opt.optimize(0, 0, 1, 0, 5).toString());
	}
	@Test
	public void test10() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL5);
		System.out.println(opt.optimize(0, 0, 1, 0, 5).toString());
	}
	@Test
	public void test11() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL7);
		System.out.println(opt.optimize(0, 0, 1, 0, 5).toString());
	}
	@Test
	public void tes12() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL9);
		System.out.println(opt.optimize(0, 0, 1, 0, 5).toString());
	}

	/// init border = 0.1
	@Test
	public void test13() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL3);
		System.out.println(opt.optimize(0, 0, 0.1, 0, 5).toString());
	}
	@Test
	public void test14() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL5);
		System.out.println(opt.optimize(0, 0, 0.1, 0, 5).toString());
	}
	@Test
	public void test15() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL7);
		System.out.println(opt.optimize(0, 0, 0.1, 0, 5).toString());
	}
	@Test
	public void tes16() {
		SimulatedAnnealing opt = new SimulatedAnnealing(Model.FIL9);
		System.out.println(opt.optimize(0, 0, 0.1, 0, 5).toString());
	}
*/
}
