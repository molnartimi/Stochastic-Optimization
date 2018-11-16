package model.spdn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;

public class SpdnModelFactory {
	private static final String MODELS_FOLDER = "..\\SPDN\\models\\";
	private static final String NAME = "name";
	private static final String PARAMETER = "SensitivityNetParameter";
	private static final String REWARD = "StateRewardConfiguration";
	private static final String SpdnModel = "SpdnModel";
	private static final String ID = "id";
	private static final String VALUE = "value";
	
	private static final Double INIT_TOL = 1e-9;
	private static final Logger logger = LoggerFactory.getLogger(SpdnModelFactory.class);
	
	public static SpdnModel createModelFromXml(String filePath, String id) {
		if (existSerializedModel(id)) {
			logger.info("Serialized model exists with id " + id);
			return loadModelFromXml(filePath, id);
		} else {
			return parseFromPnml(filePath, id);
		}
	}
	
	private static boolean existSerializedModel(String id) {
		String serializedFileName = MODELS_FOLDER + id + ".xml";
		return new File(serializedFileName).exists();
	}

	private static SpdnModel parseFromPnml(String filePath, String id) {
		logger.info("Parsing model " + filePath);
		Document xmlDocument = convertXMLFileToXMLDocument(filePath);
		
		String name = xmlDocument.getElementsByTagName(NAME).item(0).getTextContent().trim();
		List<SpdnParameter> spdnParameterList = getParameterList(xmlDocument);
		
		List<Reward> rewardList = getRewardList(xmlDocument);
		List<SpdnReward> spdnRewardList = calcExpectedRewardValues(filePath, rewardList, spdnParameterList);
			
		writeModelToXml(name, id, spdnParameterList, spdnRewardList);
		return new SpdnModel(filePath, name, id, spdnParameterList, spdnRewardList);
	}
	
	private static SpdnModel loadModelFromXml(String filePath, String id) {
		logger.info("Loading model " + id);
		
		Document xmlDocument = convertXMLFileToXMLDocument(MODELS_FOLDER + id + ".xml");
		String name = xmlDocument.getElementsByTagName(SpdnModel).item(0).getAttributes().getNamedItem(NAME).getNodeValue();
		
		NodeList params = xmlDocument.getElementsByTagName(PARAMETER);
		List<SpdnParameter> spdnParameterList = new ArrayList<>();
		for (int i = 0; i < params.getLength(); i++) {
			NamedNodeMap nodeAttributes = params.item(i).getAttributes();
			String paramName = nodeAttributes.getNamedItem(NAME).getNodeValue();
			double defaultValue = Double.valueOf(nodeAttributes.getNamedItem(VALUE).getNodeValue());
			spdnParameterList.add(new SpdnParameter(paramName, defaultValue));
		}
		
		NodeList rewards = xmlDocument.getElementsByTagName(REWARD);
		List<SpdnReward> spdnRewardList = new ArrayList<>();
		for (int i = 0; i < rewards.getLength(); i++) {
			NamedNodeMap nodeAttributes = rewards.item(i).getAttributes();
			String rewardName = nodeAttributes.getNamedItem(NAME).getNodeValue();
			double expectedValue = Double.valueOf(nodeAttributes.getNamedItem(VALUE).getNodeValue());
			spdnRewardList.add(new SpdnReward(rewardName, expectedValue));
		}
		
		return new SpdnModel(filePath, name, id, spdnParameterList, spdnRewardList);
	}

	private static Document convertXMLFileToXMLDocument(String filePath) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			Document xmlDocument = builder.parse(new File(filePath));
			return xmlDocument;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<SpdnParameter> getParameterList(Document xmlDocument) {
		List<SpdnParameter> list = new ArrayList<>();
		NodeList paramList = xmlDocument.getElementsByTagName(PARAMETER);

		for (int i = 0; i < paramList.getLength(); i++) {
			NodeList paramParams= paramList.item(i).getChildNodes();
			String name = paramParams.item(1).getFirstChild().getTextContent().trim();
			Double defaultValue = Double.valueOf(paramParams.item(5).getTextContent());
			
			list.add(new SpdnParameter(name, defaultValue));
		}
		
		return list;
	}
	
	private static List<Reward> getRewardList(Document xmlDocument) {
		List<Reward> rewardList = new ArrayList<>();
		NodeList rewardNodeList = xmlDocument.getElementsByTagName(REWARD);

		for (int i = 0; i < rewardNodeList.getLength(); i++) {
			Node rewardParams= rewardNodeList.item(i);
			String name = rewardParams.getChildNodes().item(1).getTextContent().trim();
			rewardList.add(Reward.instantaneous(name));
		}
		
		return rewardList;
	}
	
	private static List<SpdnReward> calcExpectedRewardValues(String filePath, List<Reward> simpleRewardList, List<SpdnParameter> spdnParameterList) {
		SpdnAnalysisResult rewardResults = runAnalyzerWithDefaultParamValues(filePath, simpleRewardList, spdnParameterList);
		
		List<SpdnReward> rewardHandlerList = new ArrayList<>();
		for (Reward r: simpleRewardList) {
			rewardHandlerList.add(new SpdnReward(r.getConfigurationName(), rewardResults.getValue(r)));
		}
		
		return rewardHandlerList;
	}

	private static SpdnAnalysisResult runAnalyzerWithDefaultParamValues(String filePath, List<Reward> rewardList, List<SpdnParameter> spdnParameterList) {
		List<Parameter> simpleParameterList = new ArrayList<>();
		List<Double> parameterValues = new ArrayList<>();
		
		for (SpdnParameter handler: spdnParameterList) {
			simpleParameterList.add(handler.parameter);
			parameterValues.add(handler.defaultValue);
		}
		
		logger.info("Run analyzer to get reward values at default parameter values.");
		SpdnExeRunner runner = new SpdnExeRunner(filePath, rewardList, simpleParameterList);
		return tryRunWithDefaultValues(runner, INIT_TOL, parameterValues);
	}

	private static SpdnAnalysisResult tryRunWithDefaultValues(SpdnExeRunner runner, double tolerance, List<Double> parameterValues) {
		SpdnAnalysisResult result = null;
		try {
			runner.setTolerance(tolerance);
			result = runner.run(parameterValues);
		} catch (SpdnException e) {
			if (tolerance > 0.01) {
				throw new SpdnException("Model can't be analysed with its default values!");
			} else {
				logger.info("Model analyze failed with tolerance " + tolerance);
				tryRunWithDefaultValues(runner, tolerance * 10, parameterValues);
			}
		}
		return result;
	}
	
	private static void writeModelToXml(String name, String id, List<SpdnParameter> parameterList, List<SpdnReward> rewardList) {
		try {
			logger.info("Serializing model " + id);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root element
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(SpdnModel);
			doc.appendChild(rootElement);

			// children
			rootElement.setAttribute(NAME, name);
			rootElement.setAttribute(ID, id);

			for (SpdnParameter param : parameterList) {
				Element paramsElement = doc.createElement(PARAMETER);
				paramsElement.setAttribute(NAME, param.name);
				paramsElement.setAttribute(VALUE, Double.toString(param.defaultValue));
				rootElement.appendChild(paramsElement);
			}

			for (SpdnReward reward : rewardList) {
				Element rewardElement = doc.createElement(REWARD);
				rewardElement.setAttribute(NAME, reward.name);
				rewardElement.setAttribute(VALUE, Double.toString(reward.expectedResult));
				rootElement.appendChild(rewardElement);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(MODELS_FOLDER + id + ".xml"));

			transformer.transform(source, result);
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

	}
	

}
