package algorithms;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import models.Models;
import spdn.SPDN;


/**
 * source: http://www.theprojectspot.com/tutorial-post/simulated-annealing-algorithm-for-beginners/6
 */
public class SimulatedAnnealing {
	Models model;
	SPDN spdn;
	
	private double  temp = 100;
	private double coolingRate = 0.9;
	private double border = 0.3;
	private double borderSmallerRate = 0.95;
	private int restart = 2;
	private int innerRestart = 3;
	private double tolerance = 0.001;
	
	public SimulatedAnnealing(Models model) {
		this.model = model;
		this.spdn = new SPDN(model, 0);
	}
	
	public RealVector optimize(double initTemp, double coolingRate, double border, double borderSmallerRate, int restart, double tolerance) {
		initParams(initTemp, coolingRate, border, borderSmallerRate, restart, tolerance);
		
		int i = 0;
		RealVector minPoint = null;
		double minValue = this.tolerance + 1;
		
		while (i < this.restart && minValue > this.tolerance) {
			RealVector xn = MatrixUtils.createRealVector(model.getRandomPoint());
			
			RealVector xnext = MatrixUtils.createRealVector(new double[spdn.getDimension()]);
			double fx = spdn.f(xn);
			double fxnext;
			RealVector best = xn.copy();
			double bestF = fx;
			
			while (temp > 1) {
				for(int j = 0; j < innerRestart; j++){
					for(int k = 0; k < spdn.getDimension(); k++)
						xnext.setEntry(k, (xn.getEntry(k)-border)+Math.random()*2*this.border);
					fxnext = spdn.f(xnext);
						
					if (acceptanceProbability(fx,fxnext,temp) > Math.random())
						xn = xnext.copy();
						
					if (spdn.f(xn) < bestF){
						best = xn.copy();
						bestF = spdn.f(best);
					}
				}
						
				temp *= this.coolingRate;
				this.border *= this.borderSmallerRate;
			}
			if (minPoint == null || minValue < bestF) {
				minPoint = best.copy();
				minValue = bestF;
			}
		}
		return minPoint;
	}
	
	private double acceptanceProbability(double energy, double newEnergy, double temp) {
        if (newEnergy < energy) {
            return 1.0;
        }
        double result = Math.exp((energy - newEnergy) / temp);
        return result;
    }
	
	private void initParams(double initTemp, double coolingRate, double border, double borderSmallerRate, int restart, double tolerance) {
		if (initTemp > 0) this.temp = initTemp;
		if (coolingRate > 0) this.coolingRate = coolingRate;
		if (border > 0) this.border = border;
		if (borderSmallerRate > 0) this.borderSmallerRate = borderSmallerRate;
		if (restart > 0) this.restart = restart;
		if (tolerance > 0) this.tolerance = tolerance;
	}

}
