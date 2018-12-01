package model.storm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.naming.directory.InvalidAttributeValueException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hu.bme.mit.inf.petridotnet.spdn.SpdnException;
import hu.bme.mit.modelchecker.storm.exception.StormException;
import hu.bme.mit.modelchecker.storm.model.ModelParam;
import hu.bme.mit.modelchecker.storm.model.ModelReward;
import model.spdn.SpdnModel;
import model.spdn.SpdnModelFactory;
import model.spdn.SpdnParameter;
import model.spdn.SpdnReward;

public class StormModelFactory {
	private static final String MODELS_FOLDER = "..\\STORM\\models\\";
	
	private String inputJSON, inputPRISM;
	private String name;
	private List<StormParameter> paramList;
	private List<Double> parameterValues;
	private List<String> paramNameList, rewardNameList;
	private List<StormReward> rewardList;
	private String id;
	
	private static final Double INIT_TOL = 1e-9;
	private static final Logger logger = LoggerFactory.getLogger(SpdnModelFactory.class);

	public StormModelFactory(String inputJSON, String inputPRISM, String id) {
		this.inputJSON = inputJSON;
		this.inputPRISM = inputPRISM;
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
			readPRISM();
			readJSON();
			
			createRewards();
			saveToJson();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new StormModel(inputPRISM, name, id, paramList, rewardList);
	}
	
	private void saveToJson() {
		logger.info("Serializing model to JSON");
		try (Writer writer = new FileWriter(MODELS_FOLDER + "id" + ".json")) {
	        Gson gson = new Gson();
	        SerializedJsonObject obj = new SerializedJsonObject();
	        obj.name = name;
	        obj.prismPath = inputPRISM;
	        ParameterJsonObject[] plist = new ParameterJsonObject[paramList.size()];
	        RewardJsonObject[] rlist = new RewardJsonObject[rewardList.size()];
	        int i = 0;
	        for (StormParameter p: paramList) {
	        	ParameterJsonObject pj = new ParameterJsonObject();
	        	pj.name = p.name;
	        	pj.defaultValue = p.defaultValue;
	        	pj.max = p.maxValue;
	        	pj.min = p.minValue;
	        	plist[i++] = pj;
	        }
	        i = 0;
	        for (StormReward r: rewardList) {
	        	RewardJsonObject rj = new RewardJsonObject();
	        	rj.name = r.name;
	        	rj.value = r.expectedResult;
	        	rlist[i++] = rj;
	        }
	        obj.parameters = plist;
	        obj.rewards = rlist;
	        
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
		for (String rewardName: rewardNameList) {
			stormRewards.add(new ModelReward(rewardName));
		}
		for (String paramName: paramNameList) {
			stormParams.add(new ModelParam(paramName));
		}
		StormCLIRunner runner = new StormCLIRunner(inputPRISM, stormParams, stormRewards);
		StormAnalyzerResult result = runRewardsWithDefault(runner, INIT_TOL);
		for (ModelReward r: stormRewards) {
			rewardList.add(new StormReward(r.name, result.getResultOf(r)));
		}
	}

	private void readJSON() throws FileNotFoundException, InvalidAttributeValueException {
		logger.info("Reading json file " + inputJSON);
		try (Reader reader = new InputStreamReader(new FileInputStream(new File(inputJSON)))) {
            Gson gson = new GsonBuilder().create();
            MyJsonObject jsonObj = gson.fromJson(reader, MyJsonObject.class);
            for (ParameterJsonObject p: jsonObj.parameters) {
            	if (!paramNameList.contains(p.name)) {
            		throw new InvalidAttributeValueException("Paramterer name " + p.name + " does not exist in " + inputPRISM + " model");
            	}
            	paramList.add(new StormParameter(p.name, p.defaultValue, p.min, p.max));
            	parameterValues.add(p.defaultValue);
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }

	}

	private void readPRISM() throws FileNotFoundException {
		logger.info("Reading prism file " + inputPRISM);
		FileReader prismReader = new FileReader(new File(inputPRISM));
		Scanner sc = new Scanner(prismReader);
		String line;
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.trim().indexOf("const double") == 0) {
				paramNameList.add(line.substring(line.indexOf("double") + 7, line.length() - 1));
			}
			if (line.trim().indexOf("module") == 0) {
				this.name = line.substring(line.indexOf("module") + 7).trim();
			}
			if (line.trim().indexOf("rewards") == 0) {
				this.rewardNameList.add(line.substring(line.indexOf("rewards") + 9, line.length()-1).trim());
			}
		}
		sc.close();
	}
	
	private StormAnalyzerResult runRewardsWithDefault(StormCLIRunner runner, double tolerance) throws StormException {
		logger.info("Run storm analyzer to get reward values at default parameter values.");
		StormAnalyzerResult result = null;
		try {
			runner.setTolerance(tolerance);
			result = runner.run2(parameterValues);
		} catch (StormException e) {
			if (tolerance > 0.01) {
				throw new StormException("Model can't be analysed with its default values!");
			} else {
				logger.info("Model analyze failed with tolerance " + tolerance);
				runRewardsWithDefault(runner, tolerance * 10);
			}
		}
		return result;
		
	}
	
	private StormModel loadModelFromJSON() {
		logger.info("Loading model " + id);
		
		try(Reader reader = new InputStreamReader(StormModelFactory.class.getResourceAsStream(MODELS_FOLDER + id + ".json"), "UTF-8")){
            Gson gson = new GsonBuilder().create();
            SerializedJsonObject jsonObj = gson.fromJson(reader, SerializedJsonObject.class);
            for (ParameterJsonObject p: jsonObj.parameters) {
            	paramList.add(new StormParameter(p.name, p.defaultValue, p.min, p.max));
            }
            for (RewardJsonObject r: jsonObj.rewards) {
            	rewardList.add(new StormReward(r.name, r.value));
            }
            return new StormModel(jsonObj.prismPath, name, id, paramList, rewardList);
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
		public String prismPath;
		public String name;
		public ParameterJsonObject[] parameters;
		public RewardJsonObject[] rewards;
	}
}
