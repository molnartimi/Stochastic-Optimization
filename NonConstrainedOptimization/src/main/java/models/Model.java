package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;

public enum Model {
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
			new Double[] {0.586765349081149, 4.99999999371597, 0.925618638346947, 0.997730355094315}),
	FIL3("FIL3", "philosophers_3.pnml", new String[] {"phil1_eatingRate", "phil2_eatingRate", "phil3_eatingRate"},
            new Double[] {7.33569408796258, 2.1563769289662, 9.68078350329879},
            new Double[][] {{0.7, 70.}, {0.2, 20.}, {0.9, 90.}},
            new String[] {"phil1_thinkingTime","phil2_thinkingTime","phil3_thinkingTime"},
            new Double[] {0.612806127300446, 0.539690628983682, 0.971340614292899}),
	FIL5("FIL5", "philosophers_5.pnml", new String[] {"phil1_eatingRate", "phil2_eatingRate", "phil3_eatingRate","phil4_eatingRate","phil5_eatingRate"},
            new Double[] {7.33569408796258, 2.1563769289662, 9.68078350329879, 9.64067749052976, 9.47722968486562},
            new Double[][] {{0.7, 70.}, {0.2, 20.}, {0.9, 90.}, {0.9, 90.}, {0.9, 90.}},
            new String[] {"phil1_thinkingTime","phil2_thinkingTime","phil3_thinkingTime", "phil4_thinkingTime","phil5_thinkingTime"},
            new Double[] {0.577405731389199, 0.541092302300515, 0.972871962393453, 0.606059328777654, 0.561052147014916}),
	FIL7("FIL7", "philosophers_7.pnml", new String[] {"phil1_eatingRate", "phil2_eatingRate", "phil3_eatingRate","phil4_eatingRate","phil5_eatingRate",
			"phil6_eatingRate","phil7_eatingRate"},
            new Double[] {7.33569408796258, 2.1563769289662, 9.68078350329879, 9.64067749052976, 9.47722968486562, 4.03202598762859, 3.01116461683996},
            new Double[][] {{0.7, 70.}, {0.2, 20.}, {0.9, 90.}, {0.9, 90.}, {0.9, 90.}, {0.4, 40.}, {0.3, 30.}},
            new String[] {"phil1_thinkingTime","phil2_thinkingTime","phil3_thinkingTime", "phil4_thinkingTime","phil5_thinkingTime",
            		"phil6_thinkingTime","phil7_thinkingTime"},
            new Double[] {0.50207501473619, 0.543354119312228, 0.972732152165796, 0.610779727190252, 0.518625960349654,
                    0.561970021219459, 0.469316446894766}),
	FIL9("FIL9", "philosophers_9.pnml", new String[] {"phil1_eatingRate", "phil2_eatingRate", "phil3_eatingRate","phil4_eatingRate","phil5_eatingRate",
			"phil6_eatingRate","phil7_eatingRate", "phil8_eatingRate","phil9_eatingRate"},
            new Double[] {7.33569408796258, 2.1563769289662, 9.68078350329879, 9.64067749052976, 9.47722968486562, 4.03202598762859, 3.01116461683996,
            		1.24807417152331, 6.63293781606486},
            new Double[][] {{0.7, 70.}, {0.2, 20.}, {0.9, 90.}, {0.9, 90.}, {0.9, 90.}, {0.4, 40.}, {0.3, 30.}, {0.1, 10.}, {0.6, 60.}},
            new String[] {"phil1_thinkingTime","phil2_thinkingTime","phil3_thinkingTime", "phil4_thinkingTime","phil5_thinkingTime",
            		"phil6_thinkingTime","phil7_thinkingTime", "phil8_thinkingTime","phil9_thinkingTime"},
            new Double[] {0.601260289962823, 0.540391924817233, 0.972856929631972, 0.611504020030322, 0.512104660127348,
                    0.605977871938909, 0.272811133613441, 0.206801814489149, 0.642713043549504});
	
	
	
	private String id;
	private String file;
	private List<Parameter> parameters = new ArrayList<>();
	private Map<Parameter,Double> validValues = new HashMap<>();
	private Map<Parameter,List<Double>> borders = new HashMap<>();
	private List<Reward> rewards = new ArrayList<>();
	private Map<Reward,Double> measurements = new HashMap<>();
	
	private Model(String id, String file, String[] parameters, Double[] validValues, Double[][] borders,
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
	
	public List<Double> getRandomPointList() {
		List<Double> point = new ArrayList<>();
		Random rand = new Random();
		
		for(int i = 0; i < getDim(); i++) {
			List<Double> border = borders.get(parameters.get(i));
			point.add(rand.nextDouble() * (border.get(1) - border.get(0)) + border.get(0));
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
