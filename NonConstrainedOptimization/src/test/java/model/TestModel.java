package model;

import javax.naming.directory.InvalidAttributeValueException;

import hu.bme.mit.modelchecker.storm.exception.StormException;
import model.spdn.SpdnModel;
import model.spdn.SpdnModelFactory;
import model.storm.StormModel;
import model.storm.StormModelFactory;

public enum TestModel {
	SMPL("simple-server"),
	VCLS("vcl_stochastic"),
	HYBC("hybrid-cloud"),
	FIL3("philosophers_3"),
	FIL4("philosophers_4"),
	FIL5("philosophers_5"),
	FIL6("philosophers_6"),
	FIL7("philosophers_7"),
	FIL8("philosophers_8"),
	FIL9("philosophers_9"),
	FIL10("philosophers_10"),
	FIL11("philosophers_11"),
	FIL12("philosophers_12"),
	FIL13("philosophers_13"),
	FIL14("philosophers_14"),
	FIL15("philosophers_15"),
	FIL16("philosophers_16");
	
	
	private String fileName;
	private final String spdnModelFolderPath = System.getProperty("os.name").contains("windows") ? "..\\SPDN\\models\\" : "../SPDN/models/";
	private final String stormModelFolderPath = System.getProperty("os.name").contains("windows") ? "..\\STORM\\models\\" : "../STORM/models/";
	
	private TestModel(String fileName) {
		this.fileName = fileName;
	}
	
	public SpdnModel spdnModel() {
		return SpdnModelFactory.createModelFromXml(spdnModelFolderPath + fileName + ".pnml", this.toString());
	}
	
	public StormModel stormModel() throws InvalidAttributeValueException, StormException {
		String path = stormModelFolderPath + fileName;
		return new StormModelFactory(path + ".json", path + ".prism", this.toString()).build();
	}
	
	public StormModel stormJaniModel() throws InvalidAttributeValueException, StormException {
		String path = stormModelFolderPath + fileName;
		return new StormModelFactory(path + "_bounded.jani", this.toString()).build();
	}
	
}
