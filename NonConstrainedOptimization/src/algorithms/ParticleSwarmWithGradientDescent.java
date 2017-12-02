package algorithms;

import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;

public class ParticleSwarmWithGradientDescent extends ParticleSwarm{
	public static final String ID = "PSGD";
	
	private int gradientMaxIter = 5;
	private double gamma = 1;
	
	public ParticleSwarmWithGradientDescent(Model model){
		super(model);
	}
	
	public SPDNResult optimize(int swarmSize, int maxIter, int gradientMaxIter, double gamma, double omega, double fiParticle, double fiGlobal) {
		long startTime = System.nanoTime();
		
		initParams(swarmSize, maxIter, gradientMaxIter, gamma, omega, fiParticle, fiGlobal);
		
		List<Particle> swarm = initSwarm();
		
		for(int i = 0; i < this.maxIter; i++){
			for (int j=0; j < this.swarmSize; j++){
				Particle p = swarm.get(j);
				stepParticle(p);
				if (bestValue > p.getLocalBestValue()) {
					bestValue = p.getLocalBestValue();
					bestPoint = p.getLocalBestPoint().copy();
				}
			}
			doGradientDescent();
			
		}
		
		SPDNResult result = new SPDNResult(bestValue, SPDN.convertPoint(bestPoint).toArray(), ID, getHyperParams(), model);
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
	
	protected void initParams(int swarmSize, int maxIter, int gradientMaxIter, double gamma, double omega, double fiParticle, double fiGlobal) {
		if (gradientMaxIter > 0) this.gradientMaxIter = gradientMaxIter;
		if (gamma > 0) this.gamma = gamma;
		super.initParams(swarmSize, gradientMaxIter, omega, fiParticle, fiGlobal);
	}
	
	protected void doGradientDescent() {
		Random r = new Random();
		double gamma = this.gamma;
		
		RealVector xn = bestPoint.copy();
		RealVector xBefore = MatrixUtils.createRealVector(new double[spdn.getDimension()]);
		
		try {
			for (int j = 0; j < gradientMaxIter; j++) {
				xn = xn.add(spdn.df(xn).mapMultiply(-r.nextDouble() * gamma));
				RealVector DxB = spdn.df(xBefore);
				RealVector Dxn = spdn.df(xn);
				gamma = xn.add(xBefore.mapMultiply(-1)).dotProduct(Dxn.add(DxB.mapMultiply(-1)));
				gamma /= Dxn.add(DxB.mapMultiply(-1)).getNorm() * Dxn.add(DxB.mapMultiply(-1)).getNorm();
			}
		} catch (SpdnException e) {
			// Nothing to do. xn contains the last valid point
		}
		
		double newValue = spdn.f(xn);
		if(newValue < bestValue){
			bestPoint = xn.copy();
			bestValue = newValue;
		}
	}
	
	private SortedMap<String, Double> getHyperParams() {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("swarmSize", (double) swarmSize);
		map.put("maxIter", (double) maxIter);
		map.put("omega", omega);
		map.put("fiParticle", fiParticle);
		map.put("fiGlobal", fiGlobal);
		map.put("gradientMaxIter", (double) gradientMaxIter);
		map.put("gamma", gamma);
		return map;
	}
}
