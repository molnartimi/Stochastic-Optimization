package algorithms;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;


/**
 * source: http://www.theprojectspot.com/tutorial-post/simulated-annealing-algorithm-for-beginners/6
 */
public class SimulatedAnnealing {
	public static final String ID = "SIMA";
	
	private Model model;
	SPDN spdn;
	
	private double  initTemp = 100;
	private double coolingRate = 0.9;
	private double initBorder = 0.3;
	private double borderSmallerRate = 0.95;
	private int innerRestart = 3;
	private double tolerance = 0.001;
	
	public SimulatedAnnealing(Model model) {
		this.model = model;
		this.spdn = new SPDN(model);
	}
	
	public SPDNResult optimize(double initTemp, double coolingRate, double initBorder, double borderSmallerRate, int restart) {
		long startTime = System.nanoTime();
		
		initParams(initTemp, coolingRate, initBorder, borderSmallerRate, restart);
		
		int i = 0;
		RealVector minPoint =  MatrixUtils.createRealVector(new double[model.getDim()]);
		double minValue = 10000000;
		
		while (i < restart+1 && minValue > this.tolerance) {
			RealVector xn = MatrixUtils.createRealVector(model.getRandomPoint());
			
			RealVector xnext = MatrixUtils.createRealVector(new double[spdn.getDimension()]);
			try {
				double fx = spdn.f(xn);
				double fxnext;
				RealVector best = xn.copy();
				double bestF = fx;
				
				double temp = this.initTemp;
				double border = this.initBorder;
				
				while (temp > 1) {
					for(int j = 0; j < innerRestart; j++){
						try {
							for(int k = 0; k < spdn.getDimension(); k++)
								xnext.setEntry(k, (xn.getEntry(k)-border)+Math.random()*2*border);
							fxnext = spdn.f(xnext);
							if (acceptanceProbability(fx,fxnext,temp) > Math.random())
								xn = xnext.copy();
								
							if (spdn.f(xn) < bestF){
								best = xn.copy();
								bestF = spdn.f(best);
							}
						} catch (SpdnException e) {
							// Nothing to do, go on.
						}
					}
							
					temp *= this.coolingRate;
					border *= this.borderSmallerRate;
				}
				if (minPoint == null || minValue < bestF) {
					minPoint = best.copy();
					minValue = bestF;
				}
			} catch (SpdnException e) {
				// Nothing to do, go and try again.
			}
			i++;
		}
		
		SPDNResult result = new SPDNResult(minValue, SPDN.convertPoint(minPoint).toArray(),ID, getHyperParams(), model);
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
	
	private double acceptanceProbability(double energy, double newEnergy, double temp) {
        if (newEnergy < energy) {
            return 1.0;
        }
        double result = Math.exp((energy - newEnergy) / temp);
        return result;
    }
	
	private void initParams(double initTemp, double coolingRate, double initBorder, double borderSmallerRate, int restart) {
		if (initTemp > 0) this.initTemp = initTemp;
		if (coolingRate > 0) this.coolingRate = coolingRate;
		if (initBorder > 0) this.initBorder = initBorder;
		if (borderSmallerRate > 0) this.borderSmallerRate = borderSmallerRate;
	}
	
	private SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("initTemp", initTemp);
		map.put("coolingRate", coolingRate);
		map.put("border", initBorder);
		map.put("borderSmallerRate", borderSmallerRate);
		return map;
	}

}
