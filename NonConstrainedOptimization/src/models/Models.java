package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;

public enum Models {
	SIMPLE_SERVER("SMPL", "simple-server.pnml", new String[]{"requestRate","serviceTime"}, new Double[]{1.5, 0.25},
			new Double[][] {{0.15, 15.0}, {0.025, 2.5}}, new String[] {"Idle", "ServedRequests"},
			new Double[] {0.727272727272727, 1.09090909090909}),
	VCL_STOCHASTIC("VCLS", "vcl_stochastic.pnml", new String[] {"incomingRate", "dispatchTime", "warmDispatchTime", "jobTime",
            "powerTime", "powerUsage", "idlePowerFactor"}, new Double[] {0.015, 0.5, 0.15, 60., 5., 0.75, 0.6},
			new Double[][] {{0.00015, 1.5}, {0.005, 50.}, {0.0015, 15.}, {0.6, 6000.}, {0.05, 500.}, {0.0075, 75.}, {0.006, 1.}},
			new String[] {"jobsFinished", "powerUsage", "noFreeMachines", "jobsDispatched", "machinesWorking", "hotMachinesWorking", "coldStarted"},
			new Double[] {0.0148748002933323, 5.20955485293811, 0.00209451703730028, 0.0148748002307829, 2.3647315051718, 2.17735632582612, 6.36702992684728E-7}),
	HYBRID_CLOUD("HYBC", "hybrid-cloud.pnml", new String[] {"incRate","p","lbTime","execTime1","execTime2","failRate","idleFactor","repairTime",
            "publicRent","runPower","idlePower","repairCost"},
			new Double[] {5., 0.75, 0.0002, 0.2, 0.1, 0.0002, 0.1, 24., 0.8, 0.3, 0.01, 1000.},
			new Double[][] {{1., 10.}, {0.01, 0.99}, {0.00009, 0.002}, {0.02, 0.99}, {0.01, 0.99}, {0.00009, 0.002}, {0.001,0.1},
			{20., 30.},{0.08, 2.}, {0.03, 0.99}, {0.001, 0.1}, {100., 3000.}},
			new String[] {"Expense","JobComplete","JobsProcessing","NoFailedServer"},
			new Double[] {0.586765349081149, 4.99999999371597, 0.925618638346947, 0.997730355094315});
	
	
	private String id;
	private String file;
	private List<Parameter> parameters = new ArrayList<>();
	private Map<Parameter,Double> validValues = new HashMap<>();
	private Map<Parameter,List<Double>> borders = new HashMap<>();
	private List<Reward> rewards = new ArrayList<>();
	private Map<Reward,Double> measurements = new HashMap<>();
	
	private Models(String id, String file, String[] parameters, Double[] validValues, Double[][] borders,
			String[] rewards, Double[] measurements) {
		this.id = id;
		this.file = file;
		for(int i = 0; i < parameters.length; i++) {
			this.parameters.add(Parameter.ofName(parameters[i]));
			this.validValues.put(this.parameters.get(i), validValues[i]);
			this.borders.put(this.parameters.get(i), Arrays.asList(borders[i]));
		}
		for(int i = 0; i < rewards.length; i++) {
			this.rewards.add(Reward.instantaneous(rewards[i]));
			this.measurements.put(this.rewards.get(i), measurements[i]);
		}
			
	}
	
	public String getId() { return id; }
	public String getFileName() { return file; }
	public Parameter getParam(int idx) { return parameters.get(idx); }
	public List<Parameter> getAllParams() { return parameters; }
	public double getValidValue(Parameter param) { return validValues.get(param); }
	public List<Double> getBorders(Parameter param) { return borders.get(param); }
	public List<Double> getBorders(int idx) { return borders.get(parameters.get(idx)); }
	public Reward getReward(int idx) { return rewards.get(idx); }
	public List<Reward> getAllRewards() { return rewards; }
	public double getMeasurement(Reward reward) { return measurements.get(reward); }
	public Map<Reward,Double> getAllMeasurements() { return measurements; }
	public double getMeasurement(int idx) { return measurements.get(rewards.get(idx)); }
	public int getDim() { return parameters.size(); }
	public int getRewardsCount() { return rewards.size(); }
	
	public double[] getRandomPoint() {
		double[] point = new double[getDim()];
		Random rand = new Random();
		
		for(int i = 0; i < getDim(); i++) {
			List<Double> border = borders.get(parameters.get(i));
			point[i] = rand.nextDouble() * (border.get(1) - border.get(0)) + border.get(0);
		}
		
		return point;
	}
	
	public double[] getRandomVelocity() {
		double[] point = new double[getDim()];
		Random rand = new Random();
		
		for(int i = 0; i < getDim(); i++) {
			List<Double> border = borders.get(parameters.get(i));
			double dif = border.get(1) - border.get(0);
			point[i] = rand.nextDouble() * 2 * dif - dif;
		}
		return point;
	}
}
