package algorithms.bsc.bees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import algorithms.Optimizer;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import models.Model;
import spdn.SPDNResult;
import spdn.SPDN;

public class BeesAlgorithm extends Optimizer<BeesHyperParam> {
	public static final String ID = "BEES";
	
	public BeesAlgorithm(Model model) {
		super(model);
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
	
	@Override
	public SPDNResult optimize(BeesHyperParam params) {
		long startTime = System.nanoTime();
		
		List<Bee> scouts = initSwarm(params.scoutSize);
		double radius = params.radius;
		
		BeeComparator comparator = new BeeComparator();
		Collections.sort(scouts, comparator);
		
		for (int iter = 0; iter < params.maxIter; iter++) {
			calculateLeadsAndRecruiteds(scouts, 0, params.eliteBeesSize, params.recruitedOfElitesSize, radius);
			calculateLeadsAndRecruiteds(scouts, params.eliteBeesSize, params.bestBeesSize, params.recruitedOfBestsSize, radius);
			randomizeOtherScouts(scouts, params.eliteBeesSize + params.bestBeesSize);

			Collections.sort(scouts, comparator);
			
			radius *= params.radiusSmallerRate;
		}
		
		SPDNResult result= new SPDNResult(scouts.get(0).getVal(),
										  SPDN.convertPoint(scouts.get(0).getPos()).toArray(), 
										  ID, 
										  params.getHyperParams(),
										  model);
		
		result.setTime(System.nanoTime() - startTime);
		result.writeToCsv();
		spdn.writeCountedDataToCsv(ID);
		return result;
	}
	
	private void calculateLeadsAndRecruiteds(List<Bee> scouts, int fromIdx, int count, int recruitedSize, double radius) {
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
	
	private List<Bee> initSwarm(int scoutSize){
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

}
