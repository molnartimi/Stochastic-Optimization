package spdn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hu.bme.mit.inf.petridotnet.spdn.AnalysisResult;
import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;
import hu.bme.mit.inf.petridotnet.spdn.SpdnException;

public class SpdnModelParser {
	private static final String NAME = "name";
	private static final String PARAMETER = "SensitivityNetParameter";
	private static final String REWARD = "StateRewardConfiguration";
	private static final Double INIT_TOL = 1e-9;
	
	private String filePath;
	private Document xmlDocument;
	private List<SpdnParameter> parameterList;
	private List<SpdnReward> rewardList;
	
	public SpdnModel createModelFromXml(String filePath, String id) {
		// TODO logger
		System.out.println(id + " started to be parsed");
		this.filePath = filePath;
		xmlDocument = convertXMLFileToXMLDocument();
		
		String name = getName();
		parameterList = getParameterList();
		rewardList = getRewardList();
		
		System.out.println(id + " finished parsing");
		return new SpdnModel(filePath, name, id, parameterList, rewardList);
	}
	
	private Document convertXMLFileToXMLDocument()
	{
	    //Parser that produces DOM object trees from XML content
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	     
	    //API to obtain DOM Document instance
	    DocumentBuilder builder = null;
	    try
	    {
	        //Create DocumentBuilder with default configuration
	        builder = factory.newDocumentBuilder();
	         
	        //Parse the content to Document object
	        Document xmlDocument = builder.parse(new File(filePath));
	         
	        return xmlDocument;
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	private String getName() {
		return xmlDocument.getElementsByTagName(NAME).item(0).getTextContent().trim();
	}
	
	private List<SpdnParameter> getParameterList() {
		List<SpdnParameter> list = new ArrayList<>();
		NodeList paramList = xmlDocument.getElementsByTagName(PARAMETER);

		for (int i = 0; i < paramList.getLength(); i++) {
			NodeList paramParams= paramList.item(i).getChildNodes();
			String name = paramParams.item(1).getFirstChild().getTextContent().trim();
			Double defaultValue = Double.valueOf(paramParams.item(5).getTextContent());
			
			list.add(new SpdnParameter(Parameter.ofName(name), defaultValue));
		}
		
		return list;
	}
	
	private List<SpdnReward> getRewardList() {
		List<Reward> rewardList = new ArrayList<>();
		NodeList rewardNodeList = xmlDocument.getElementsByTagName(REWARD);

		for (int i = 0; i < rewardNodeList.getLength(); i++) {
			Node rewardParams= rewardNodeList.item(i);
			String name = rewardParams.getChildNodes().item(1).getTextContent().trim();
			rewardList.add(Reward.instantaneous(name));
		}
		
		AnalysisResult rewardResults = calcRewardResultsAtDefaultParamValues(rewardList);
		
		List<SpdnReward> rewardHandlerList = new ArrayList<>();
		for (Reward r: rewardList) {
			rewardHandlerList.add(new SpdnReward(r.getConfigurationName(), rewardResults.getValue(r)));
		}
		
		return rewardHandlerList;
	}

	private AnalysisResult calcRewardResultsAtDefaultParamValues(List<Reward> rewardList) {
		List<Parameter> simpleParameterList = new ArrayList<>();
		List<Double> parameterValues = new ArrayList<>();
		
		for (SpdnParameter handler: parameterList) {
			simpleParameterList.add(handler.parameter);
			parameterValues.add(handler.defaultValue);
		}
		
		SpdnExeRunner runner = new SpdnExeRunner(filePath, rewardList, simpleParameterList);
		return tryRunWithDefaultValues(runner, INIT_TOL, parameterValues);
	}

	private AnalysisResult tryRunWithDefaultValues(SpdnExeRunner runner, double tolerance, List<Double> parameterValues) {
		AnalysisResult result = null;
		try {
			runner.setTolerance(tolerance);
			result = runner.run(parameterValues);
		} catch (SpdnException e) {
			if (tolerance > 0.01) {
				throw new SpdnException("Model can't be analysed with its default values!");
			} else {
				tryRunWithDefaultValues(runner, tolerance * 10, parameterValues);
			}
		}
		return result;
	}
	

}
