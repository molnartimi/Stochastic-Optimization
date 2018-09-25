package algorithms.bsc.psgd;

import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import algorithms.bsc.pso.ParticleSwarm;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;

public class ParticleSwarmWithGradientDescent extends ParticleSwarm {
	public static final String ID = "PSGD";
	
	public ParticleSwarmWithGradientDescent(Model model){
		super(model);
	}
	
	public SPDNResult optimize(PSGDHyperParam params) {
		long startTime = System.nanoTime();
		
		List<Particle> swarm = initSwarm(params.swarmSize);
		
		for(int i = 0; i < params.maxIter; i++){
			for (int j=0; j < params.swarmSize; j++){
				Particle p = swarm.get(j);
				stepParticle(p, params.omega, params.fiParticle, params.fiGlobal);
				if (bestValue > p.getLocalBestValue()) {
					bestValue = p.getLocalBestValue();
					bestPoint = p.getLocalBestPoint().copy();
				}
			}
			doGradientDescent(params.gamma, params.gradientMaxIter);
			
		}
		
		SPDNResult result = new SPDNResult(bestValue, SPDN.convertPoint(bestPoint).toArray(), ID, params.getHyperParams(), model);
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
	
	protected void doGradientDescent(double gamma, int gradientMaxIter) {
		Random r = new Random();
		
		RealVector xn = bestPoint.copy();
		RealVector xBefore = MatrixUtils.createRealVector(new double[spdn.getDimension()]);
		
		try {
			for (int j = 0; j < gradientMaxIter; j++) {
				xn = xn.add(spdn.df(xn).mapMultiply(-r.nextDouble() * gamma));
				RealVector DxB = spdn.df(xBefore);
				RealVector Dxn = spdn.df(xn);
				gamma = xn.add(xBefore.mapMultiply(-1)).dotProduct(Dxn.add(DxB.mapMultiply(-1)));
				gamma /= Dxn.add(DxB.mapMultiply(-1)).getNorm() * Dxn.add(DxB.mapMultiply(-1)).getNorm();
				
				double newValue = spdn.f(xn);
				if(newValue < bestValue){
					bestPoint = xn.copy();
					bestValue = newValue;
				}
			}
		} catch (SpdnException e) {
			// Nothing to do. xn contains the last valid point
		}
	}
}
