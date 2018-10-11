package spdn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hu.bme.mit.inf.petridotnet.spdn.AnalysisResult;
import hu.bme.mit.inf.petridotnet.spdn.Parameter;
import hu.bme.mit.inf.petridotnet.spdn.Reward;

public class SPDNModelFactory {
	private static final String NAME = "name";
	private static final String PARAMETER = "SensitivityNetParameter";
	private static final String REWARD = "StateRewardConfiguration";
	
	public static SPDNModel createModelFromXml(String filePath, String id) {
		Document xmlDocument = convertXMLFileToXMLDocument(filePath);
		String name = getName(xmlDocument);
		List<ParameterHandler> parameterList = getParameterList(xmlDocument);
		List<RewardHandler> rewardList = getRewardList(xmlDocument, filePath, parameterList);
		return new SPDNModel(filePath, name, id, parameterList, rewardList);
	}
	
	private static Document convertXMLFileToXMLDocument(String filePath)
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
	
	private static String getName(Document xmlDocument) {
		return xmlDocument.getElementsByTagName(NAME).item(0).getTextContent().trim();
	}
	
	private static List<ParameterHandler> getParameterList(Document xmlDocument) {
		List<ParameterHandler> list = new ArrayList<>();
		NodeList paramList = xmlDocument.getElementsByTagName(PARAMETER);

		for (int i = 0; i < paramList.getLength(); i++) {
			NodeList paramParams= paramList.item(i).getChildNodes();
			String name = paramParams.item(1).getFirstChild().getTextContent().trim();
			Double defaultValue = Double.valueOf(paramParams.item(5).getTextContent());
			
			list.add(new ParameterHandler(Parameter.ofName(name), defaultValue));
		}
		
		return list;
	}
	
	private static List<RewardHandler> getRewardList(Document xmlDocument, String filePath, List<ParameterHandler> parameterHandlerList) {
		List<Reward> rewardList = new ArrayList<>();
		NodeList rewardNodeList = xmlDocument.getElementsByTagName(REWARD);

		for (int i = 0; i < rewardNodeList.getLength(); i++) {
			Node rewardParams= rewardNodeList.item(i);
			String name = rewardParams.getChildNodes().item(1).getTextContent().trim();
			rewardList.add(Reward.instantaneous(name));
		}
		
		AnalysisResult rewardResults = calcRewardResultsAtDefaultParamValues(filePath, rewardList, parameterHandlerList);
		
		List<RewardHandler> rewardHandlerList = new ArrayList<>();
		for (Reward r: rewardList) {
			rewardHandlerList.add(new RewardHandler(r.getConfigurationName(), rewardResults.getValue(r)));
		}
		
		return rewardHandlerList;
	}

	private static AnalysisResult calcRewardResultsAtDefaultParamValues(String filePath, List<Reward> rewardList, List<ParameterHandler> parameterHandlerList) {
		List<Parameter> parameterList = new ArrayList<>();
		List<Double> parameterValues = new ArrayList<>();
		
		for (ParameterHandler handler: parameterHandlerList) {
			parameterList.add(handler.parameter);
			parameterValues.add(handler.defaultValue);
		}
		
		return SpdnExeRunner.run(filePath, rewardList, parameterList, parameterValues);
	}
	

}
