package algorithms.bsc.simann;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import algorithms.Optimizer;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;


/**
 * source: http://www.theprojectspot.com/tutorial-post/simulated-annealing-algorithm-for-beginners/6
 */
public class SimulatedAnnealing extends Optimizer<SimAnnHyperParam> {
	public static final String ID = "SIMA";
	
	public SimulatedAnnealing(Model model) {
		super(model);
	}
	
	public SPDNResult optimize(SimAnnHyperParam params) {
		long startTime = System.nanoTime();
		
		int i = 0;
		RealVector minPoint =  MatrixUtils.createRealVector(new double[model.getDim()]);
		double minValue = 10000000;
		
		while (i < params.restart+1 && minValue > params.tolerance) {
			RealVector xn = MatrixUtils.createRealVector(model.getRandomPoint());
			
			RealVector xnext = MatrixUtils.createRealVector(new double[spdn.getDimension()]);
			try {
				double fx = spdn.f(xn);
				double fxnext;
				RealVector best = xn.copy();
				double bestF = fx;
				
				double temp = params.initTemp;
				double border = params.initBorder;
				
				while (temp > 1) {
					for(int j = 0; j < params.innerRestart; j++){
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
							
					temp *= params.coolingRate;
					border *= params.borderSmallerRate;
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
		
		SPDNResult result = new SPDNResult(minValue, SPDN.convertPoint(minPoint).toArray(),ID, params.getHyperParams(), model);
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
}
