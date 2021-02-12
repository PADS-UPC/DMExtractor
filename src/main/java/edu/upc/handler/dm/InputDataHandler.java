package edu.upc.handler.dm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.upc.entities.Activity;
import edu.upc.entities.Token;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.DrgElement;
import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.freelingutils.ActionType;
import edu.upc.freelingutils.dm.DmnFilesUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;

public class InputDataHandler {
	private LinkedHashMap<String, Activity> activitiesList;
	private Functions functions;

	public InputDataHandler(LinkedHashMap<String, Activity> activitiesList, LinkedHashMap<String, Token> tokens,
			List<Tree> trees) throws IOException {
		this.activitiesList = activitiesList;
		this.functions = new Functions(tokens, trees, activitiesList);
	}

	public LinkedHashMap<String, InputData_Dmn> getInputDataFromUnderInputData(String inputToken, Tree tree,
			Decision_Dmn decision, String decisionActionToken) throws IOException {
		LinkedHashMap<String, InputData_Dmn> inputDataList = new LinkedHashMap<String, InputData_Dmn>();
		ArrayList<String> patternStrList = new ArrayList<String>();
		patternStrList = DmnFreelingUtils.readPatternFile(DmnFilesUrl.INPUTDATA_UNDERINPUTDATA_PATTERN.toString());
		for (String patternStr : patternStrList) {
			patternStr = patternStr.replace("[inputDataToken]", inputToken);
			TregexPattern pattern = TregexPattern.compile(patternStr);
			TregexMatcher matcher = pattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree tResult = matcher.getNode("result");
				if (tResult != null) {
					String resultToken = DmnFreelingUtils.getTokenFromNode(tResult.label().value());
					if (resultToken != null) {
						String id = resultToken;// + "_" + decision.getDrgElement().getToken();
						InputData_Dmn inputData = new InputData_Dmn(
								resultToken, decision, new DrgElement(resultToken,
										functions.getInputDataName(resultToken), ActionType.INPUTDATA),
								activitiesList.get(decisionActionToken).getAction());
						inputDataList.put(id, inputData);
						System.out.println(
								"> InputData: " + inputData.getId() + " - " + inputData.getDrgElement().getName());
					}
				}
			}
		}
		return inputDataList;

	}
}
