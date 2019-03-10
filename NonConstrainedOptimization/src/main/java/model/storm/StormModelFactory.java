package model.storm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.naming.directory.InvalidAttributeValueException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hu.bme.mit.modelchecker.storm.exception.StormException;
import hu.bme.mit.modelchecker.storm.model.ModelParam;
import hu.bme.mit.modelchecker.storm.model.ModelReward;
import hu.bme.mit.theta.interchange.jani.model.Model;
import hu.bme.mit.theta.interchange.jani.model.json.JaniModelMapper;
import hu.bme.mit.theta.interchange.jani.model.BoundedType;
import hu.bme.mit.theta.interchange.jani.model.ConstantDeclaration;
import hu.bme.mit.theta.interchange.jani.model.FilterExpression;
import hu.bme.mit.theta.interchange.jani.model.Identifier;
import hu.bme.mit.theta.interchange.jani.model.Property;
import hu.bme.mit.theta.interchange.jani.model.RealConstant;
import hu.bme.mit.theta.interchange.jani.model.RealType;
import hu.bme.mit.theta.interchange.jani.model.UnaryPropertyExpression;

public class StormModelFactory {
	// TODO Linux+windows compatibility!!
	private static final String MODELS_FOLDER = "../STORM/models/";

	private String inputJSON, inputMODEL;
	private String name;
	private List<StormParameter> paramList;
	private List<Double> parameterValues;
	private List<String> paramNameList, rewardNameList;
	private List<StormReward> rewardList;
	private String id;

	private static final Logger logger = LoggerFactory.getLogger(StormModelFactory.class);

	public StormModelFactory(String inputJSON, String inputMODEL, String id) {
		this(inputMODEL, id);
		this.inputJSON = inputJSON;
	}
	
	public StormModelFactory(String inputMODEL, String id) {
		this.inputMODEL = inputMODEL;
		this.id = id;
		paramList = new ArrayList<>();
		paramNameList = new ArrayList<>();
		rewardList = new ArrayList<>();
		rewardNameList = new ArrayList<>();
		parameterValues = new ArrayList<>();
	}

	public StormModel build() throws InvalidAttributeValueException, StormException {
		if (existSerializedModel()) {
			return loadModelFromJSON();
		}
		try {
			readModel();

			createRewards();
			saveToJson();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new StormModel(inputMODEL, name, id, paramList, rewardList);
	}

	private void saveToJson() {
		logger.info("Serializing model to JSON");
		try (Writer writer = new FileWriter(MODELS_FOLDER + id + ".json")) {
			Gson gson = new Gson();
			SerializedJsonObject obj = new SerializedJsonObject();
			obj.name = name;
			obj.modelPath = inputMODEL;
			ParameterJsonObject[] plist = new ParameterJsonObject[paramList.size()];
			RewardJsonObject[] rlist = new RewardJsonObject[rewardList.size()];
			int i = 0;
			for (StormParameter p : paramList) {
				ParameterJsonObject pj = new ParameterJsonObject();
				pj.name = p.name;
				pj.defaultValue = p.defaultValue;
				pj.max = p.maxValue;
				pj.min = p.minValue;
				plist[i++] = pj;
			}
			i = 0;
			for (StormReward r : rewardList) {
				RewardJsonObject rj = new RewardJsonObject();
				rj.name = r.name;
				rj.value = r.expectedResult;
				rlist[i++] = rj;
			}
			obj.parameters = plist;
			obj.rewards = rlist;
			gson.toJson(obj, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean existSerializedModel() {
		String serializedFileName = MODELS_FOLDER + id + ".json";
		return new File(serializedFileName).exists();
	}

	private void createRewards() throws StormException {
		List<ModelReward> stormRewards = new ArrayList<>();
		List<ModelParam> stormParams = new ArrayList<>();
		for (String rewardName : rewardNameList) {
			stormRewards.add(new ModelReward(rewardName));
		}
		if (paramList.isEmpty()) {
			for (String paramName : paramNameList) {
				stormParams.add(new ModelParam(paramName));
			}
		} else {
			for (StormParameter param: paramList) {
				stormParams.add(new ModelParam(param.name));
			}
		}
		StormCLIRunner runner = new StormCLIRunner(inputMODEL, stormParams, stormRewards);
		StormAnalyzerResult result = runRewardsWithDefault(runner);
		for (ModelReward r : stormRewards) {
			rewardList.add(new StormReward(r.name, result.getResultOf(r)));
		}
	}

	private void readJSON() throws FileNotFoundException, InvalidAttributeValueException {
		logger.info("Reading json file " + inputJSON);
		try (Reader reader = new InputStreamReader(new FileInputStream(new File(inputJSON)))) {
			Gson gson = new GsonBuilder().create();
			MyJsonObject jsonObj = gson.fromJson(reader, MyJsonObject.class);
			for (ParameterJsonObject p : jsonObj.parameters) {
				if (!paramNameList.contains(p.name)) {
					throw new InvalidAttributeValueException(
							"Paramterer name " + p.name + " does not exist in " + inputMODEL + " model");
				}
				paramList.add(new StormParameter(p.name, p.defaultValue, p.min, p.max));
				parameterValues.add(p.defaultValue);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void readModel() throws FileNotFoundException, InvalidAttributeValueException {
		String fileExtension = inputMODEL;
		while (fileExtension.contains(".")) {
			fileExtension = fileExtension.substring(fileExtension.indexOf(".") + 1);
		}
		switch (fileExtension) {
		case "jani":
			readJani();
			break;
		case "prism":
		case "sm":
			readPrism();
			readJSON();
			break;
		default:
			throw new IllegalArgumentException("File extension should be jani, prism or sm");
		}
	}
	
	private void readPrism() throws FileNotFoundException {
		logger.info("Reading prism file " + inputMODEL);
		FileReader prismReader = new FileReader(new File(inputMODEL));
		Scanner sc = new Scanner(prismReader);
		String line;
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.trim().indexOf("const double") == 0 && line.trim().indexOf("=") < 0) {
				paramNameList.add(line.substring(line.indexOf("double") + 7, line.length() - 1));
			}
			if (line.trim().indexOf("module") == 0) {
				this.name = line.substring(line.indexOf("module") + 7).trim();
			}
			if (line.trim().indexOf("rewards") == 0) {
				this.rewardNameList.add(line.substring(line.indexOf("rewards") + 9, line.length() - 1).trim());
			}
		}
		sc.close();
	}
	
	private void readJani() throws FileNotFoundException {
		logger.info("Reading jani file " + inputMODEL);
		JaniModelMapper mapper = new JaniModelMapper();
		try {
			Model model = mapper.readValue(new File(inputMODEL), Model.class);
			name = model.getSystem().getElements().get(0).getAutomaton();
			
			ArrayList<ConstantDeclaration> toDelete = new ArrayList<ConstantDeclaration>();
			ArrayList<ConstantDeclaration> toAdd = new ArrayList<ConstantDeclaration>();
			
			for (ConstantDeclaration c : model.getConstants()) {
				if (c.getValue() == null) {
					if (c.getType() instanceof BoundedType) {
						BoundedType type = (BoundedType) c.getType();
						RealConstant upper = (RealConstant) type.getUpperBound();
						RealConstant lower = (RealConstant) type.getLowerBound();
						Double defaultValue = new Double(Double.valueOf(c.getComment()));
						paramList.add(new StormParameter(c.getName(), /*TODO ilyen nem kéne bele*/ defaultValue, lower.getValue(), upper.getValue()));
						parameterValues.add(defaultValue);
						
						toDelete.add(c);
						toAdd.add(new ConstantDeclaration(c.getName(), RealType.INSTANCE, null, null));
					} else {
						throw new IllegalArgumentException("Bounded type constants are needed for optimization in Jani model");
					}
				}
			}

			for (Property reward : model.getProperties()) {
				if (reward.getExpression() instanceof FilterExpression) {
					FilterExpression filterExp = (FilterExpression) reward.getExpression();
					if (filterExp.getValues() instanceof UnaryPropertyExpression) {
						UnaryPropertyExpression exp = (UnaryPropertyExpression) filterExp.getValues();
						if (exp.getExp() instanceof Identifier) {
							Identifier rewardName = (Identifier) exp.getExp();
							rewardNameList.add(rewardName.getName());
						}
					}
				}
			}
			
			model.getConstants().removeAll(toDelete);
			model.getConstants().addAll(toAdd);
			inputMODEL = inputMODEL.replace(".jani", "_mod.jani");
			mapper.writeValue(new OutputStreamWriter(new FileOutputStream(inputMODEL)), model);      
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private StormAnalyzerResult runRewardsWithDefault(StormCLIRunner runner) throws StormException {
		logger.info("Run storm analyzer to get reward values at default parameter values.");
		return runner.run(parameterValues);
	}

	private StormModel loadModelFromJSON() {
		logger.info("Loading model " + id);

		try (Reader reader = new InputStreamReader(new FileInputStream(new File(MODELS_FOLDER + id + ".json")),
				"UTF-8")) {
			Gson gson = new GsonBuilder().create();
			SerializedJsonObject jsonObj = gson.fromJson(reader, SerializedJsonObject.class);
			for (ParameterJsonObject p : jsonObj.parameters) {
				paramList.add(new StormParameter(p.name, p.defaultValue, p.min, p.max));
			}
			for (RewardJsonObject r : jsonObj.rewards) {
				rewardList.add(new StormReward(r.name, r.value));
			}
			return new StormModel(jsonObj.modelPath, jsonObj.name, id, paramList, rewardList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class ParameterJsonObject {
		public String name;
		public double min, max, defaultValue;
	}

	private class RewardJsonObject {
		public String name;
		public double value;
	}

	private class MyJsonObject {
		public ParameterJsonObject[] parameters;
	}

	private class SerializedJsonObject {
		public String modelPath;
		public String name;
		public ParameterJsonObject[] parameters;
		public RewardJsonObject[] rewards;
	}
}
