package algorithms;import java.util.ArrayList;import java.util.List;import java.util.Random;import java.util.SortedMap;import java.util.TreeMap;import org.apache.commons.math3.linear.MatrixUtils;import org.apache.commons.math3.linear.RealVector;import hu.bme.mit.inf.petridotnet.spdn.SpdnException;import models.Model;import spdn.SPDNResult;import spdn.SPDN;public class ParticleSwarm{	public static final String ID = "PSOP";		protected SPDN spdn;	protected Model model;		protected RealVector bestPoint = null;	protected double bestValue;		protected int swarmSize = 20;	protected int maxIter = 20;	protected double tolerance = 0.001;	protected double omega = 0.2;	protected double fiParticle = 0.4;	protected double fiGlobal = 0.8;		public ParticleSwarm(Model model) {		this.model = model;		this.spdn = new SPDN(model);	}	protected class Particle {		private RealVector x;		private RealVector v;		private RealVector PBestPoint;		private double PBestValue;		public Particle(RealVector pos, RealVector vel) {			x = pos;			PBestPoint = x.copy();			v = vel;			try {				PBestValue = spdn.f(PBestPoint);			} catch (SpdnException e) {				PBestValue = 1000000;			}		}		public RealVector getLocalBestPoint() {			return PBestPoint;		}		public double getLocalBestValue() {			return PBestValue;		}		public RealVector getV() {			return v;		}		public RealVector getX() {			return x;		}		public void step(RealVector newX, RealVector newV) {			x = newX.copy();			v = newV.copy();			try {				double value = spdn.f(x);				if (value < PBestValue) {					PBestValue = value;					PBestPoint = x.copy();				}			} catch (SpdnException e) {				// Don't refresh best point.			}		}	}	public SPDNResult optimize(int swarmSize, int maxIter, double omega, double fiParticle, double fiGlobal) {		long startTime = System.nanoTime();				initParams(swarmSize, maxIter, omega, fiParticle, fiGlobal);				List<Particle> swarm = initSwarm();		for (int i = 0; i < this.maxIter; i++) {			for (int j = 0; j < this.swarmSize; j++) {				Particle p = swarm.get(j);				stepParticle(p);				if (bestValue > p.getLocalBestValue()) {					bestValue = p.getLocalBestValue();					bestPoint = p.getLocalBestPoint().copy();				}			}		}				SPDNResult result = new SPDNResult(bestValue, SPDN.convertPoint(bestPoint).toArray(), ID, getHyperParams(), model);		result.setTime(System.nanoTime() - startTime);		result.writeToCsv();		spdn.writeCountedDataToCsv(ID);		return result;	}		protected void stepParticle(Particle p) {		Random r = new Random();				RealVector x = p.getX().copy();		RealVector v = p.getV().copy();		RealVector best = p.getLocalBestPoint().copy();		for (int d = 0; d < spdn.getDimension(); d++) {			v.setEntry(d, omega * v.getEntry(d) + fiParticle * r.nextDouble() * (best.getEntry(d) - x.getEntry(d))					+ fiGlobal * r.nextDouble() * (bestPoint.getEntry(d) - x.getEntry(d)));			x.setEntry(d, x.getEntry(d) + v.getEntry(d));		}		p.step(x, v);	}		protected void initParams(int swarmSize, int maxIter, double omega, double fiParticle, double fiGlobal) {		if (swarmSize > 0) this.swarmSize = swarmSize;		if (maxIter > 0) this.maxIter = maxIter;		if (omega > 0) this.omega = omega;		if (fiParticle > 0) this.fiParticle = fiParticle;		if (fiGlobal > 0) this.fiGlobal = fiGlobal;	}		protected List<Particle> initSwarm() {		List<Particle> swarm = new ArrayList<>();				for (int i = 0; i < swarmSize; i++) {			RealVector pos = MatrixUtils.createRealVector(model.getRandomPoint());			RealVector vel = MatrixUtils.createRealVector(model.getRandomVelocity());			Particle p = new Particle(pos, vel);			swarm.add(p);			if (bestPoint == null || p.getLocalBestValue() < bestValue) {				bestPoint = pos.copy();				bestValue = p.getLocalBestValue();			}		}		return swarm;	}		private SortedMap<String, Double> getHyperParams() {		TreeMap<String, Double> map = new TreeMap<>();		map.put("swarmSize", (double) swarmSize);		map.put("maxIter", (double) maxIter);		map.put("omega", omega);		map.put("fiParticle", fiParticle);		map.put("fiGlobal", fiGlobal);		return map;	}	}