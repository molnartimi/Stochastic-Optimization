package model;

import spdn.SPDNModel;
import spdn.SPDNModelFactory;

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
	private final String modelFolderPath = "..\\SPDN\\models\\";
	
	private TestModel(String fileName) {
		this.fileName = fileName;
	}
	
	public SPDNModel model() {
		return SPDNModelFactory.createModelFromXml(modelFolderPath + fileName + ".pnml", this.toString());
	}
}
