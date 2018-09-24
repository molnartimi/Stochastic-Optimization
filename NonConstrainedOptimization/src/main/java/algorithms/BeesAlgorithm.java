package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

public class BeesAlgorithm{
	public static final String ID = "BEES";
	
	private Model model;
	private SPDN spdn;
	
	private int scoutSize = 20;
	private int bestBeesSize = 8;
	private int eliteBeesSize = 3;
	private int recruitedOfBestsSize = 5;
	private int recruitedOfElitesSize = 10;
	
	private int maxIter = 20;
	private double radius = 0.5;
	private double radiusSmallerRate = 0.9;
	
	public BeesAlgorithm(Model model) {
		this.model = model;
		this.spdn = new SPDN(model);
	}
	
	private class Bee{
		private RealVector pos;
		private double value;
		
		public Bee(RealVector p, double val){
			pos = p;
			value = val;
		}
		
		public void setPos(RealVector p, double val){
			pos = p;
			value = val;
		}
		
		public RealVector getPos(){
			return pos;
		}
		
		public double getVal(){
			return value;
		}
	}

	private class BeeComparator implements Comparator<Bee> {
		@Override
		public int compare(Bee b1, Bee b2) {
			double result = b1.getVal() - b2.getVal();
			if (result < 0)
				return -1;
			else if (result == 0)
				return 0;
			else
				return 1;
		}
	}
	
	public SPDNResult optimize(int maxIter, double initRadius, double radiusSmallerRate, 
			int scoutSize, int bestBeesSize, int eliteBeesSize, 
			int recruitedOfBestsSize, int recruitedOfElitesSize) {
		long startTime = System.nanoTime();
		
		initParams(maxIter, initRadius, radiusSmallerRate, scoutSize, bestBeesSize, eliteBeesSize, recruitedOfBestsSize, recruitedOfElitesSize);
		List<Bee> scouts = initSwarm();
		
		BeeComparator comparator = new BeeComparator();
		Collections.sort(scouts, comparator);
		
		for (int iter = 0; iter < this.maxIter; iter++) {
			calculateLeadsAndRecruiteds(scouts, 0, this.eliteBeesSize, this.recruitedOfElitesSize);
			calculateLeadsAndRecruiteds(scouts, this.eliteBeesSize, this.bestBeesSize, this.recruitedOfBestsSize);
			randomizeOtherScouts(scouts, this.eliteBeesSize + this.bestBeesSize);

			Collections.sort(scouts, comparator);
			
			radius *= this.radiusSmallerRate;
		}
		
		SPDNResult result= new SPDNResult(scouts.get(0).getVal(),
										  SPDN.convertPoint(scouts.get(0).getPos()).toArray(), 
										  ID, 
										  getHyperParams(initRadius),
										  model);
		
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
	
	private void calculateLeadsAndRecruiteds(List<Bee> scouts, int fromIdx, int count, int recruitedSize) {
		Random r = new Random();
		
		for (int i = fromIdx; i < fromIdx + count; i++) {
			RealVector localBest = scouts.get(i).getPos().copy();
			
			for (int j = 0; j < recruitedSize; j++) {
				RealVector pos = MatrixUtils.createRealVector(new double[spdn.getDimension()]);
				
				for (int d = 0; d < spdn.getDimension(); d++) {
					pos.setEntry(d, localBest.getEntry(d) + (r.nextDouble()*2-1) * radius);
				}
				
				try {
					double newPosValue = spdn.f(pos);
					if (newPosValue < scouts.get(i).getVal()) {
						localBest = pos;
						scouts.set(i, new Bee(pos, newPosValue));
					}
				} catch (SpdnException e) {
					// Nothing to do if position of recruited is an unfeasible point
				}
				
			}
		}
	}
	
	private void randomizeOtherScouts(List<Bee> scouts, int fromIdx) {
		for (int i = fromIdx; i < scouts.size(); i++) {
			RealVector pos = MatrixUtils.createRealVector(model.getRandomPoint());
			try {
				scouts.get(i).setPos(pos, spdn.f(pos));
			} catch (SpdnException e) {
				scouts.get(i).setPos(pos, 100000000);
			}
		}
	}
	
	private void initParams(int maxIter, double initRadius, double radiusSmallerRate, int scoutSize, int bestBeesSize, int eliteBeesSize, int recruitedOfBestsSize, int recruitedOfElitesSize) {
		if (maxIter > 0) this.maxIter = maxIter;
		if (initRadius > 0) this.radius = initRadius;
		if (radiusSmallerRate > 0) this.radiusSmallerRate = radiusSmallerRate;
		if (scoutSize > 0) this.scoutSize = scoutSize;
		if (bestBeesSize > 0) this.bestBeesSize = bestBeesSize;
		if (bestBeesSize > 0) this.bestBeesSize = bestBeesSize;
		if (eliteBeesSize > 0) this.eliteBeesSize = eliteBeesSize;
		if (recruitedOfBestsSize > 0) this.recruitedOfBestsSize = recruitedOfBestsSize;
		if (recruitedOfElitesSize > 0) this.recruitedOfElitesSize = recruitedOfElitesSize;
	}
	
	private List<Bee> initSwarm(){
		ArrayList<Bee> swarm = new ArrayList<>();
		
		for(int i=0; i<scoutSize; i++) {
			RealVector pos = MatrixUtils.createRealVector(model.getRandomPoint());
			try {
				swarm.add(new Bee(pos, spdn.f(pos)));
			} catch (SpdnException e) {
				swarm.add(new Bee(pos, 100000000));
			}
		}
		return swarm;
	}
	
	private SortedMap<String, Double> getHyperParams(double initRadius) {
		TreeMap<String, Double> map = new TreeMap<>();
		map.put("maxIter", (double) this.maxIter);
		map.put("initRadius", initRadius);
		map.put("radiusSmallerRate", this.radiusSmallerRate);
		map.put("scoutSize", (double) this.scoutSize);
		map.put("bestBeesSize", (double) this.bestBeesSize);
		map.put("eliteBeesSize", (double) this.eliteBeesSize);
		map.put("recruitedOfBestsSize", (double) this.recruitedOfBestsSize);
		map.put("recruitedOfElitesSize", (double) this.recruitedOfElitesSize);
		return map;
	}
}
