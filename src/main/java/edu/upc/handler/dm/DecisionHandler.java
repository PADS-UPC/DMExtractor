package edu.upc.handler.dm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.upc.entities.Activity;
import edu.upc.entities.Predicate;
import edu.upc.entities.PredicateArgument;
import edu.upc.entities.Token;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.DrgElement;
import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.entities.dm.OutputEntry_Rule;
import edu.upc.parserutils.ActionType;
import edu.upc.parserutils.dm.DmnFilesUrl;
import edu.upc.parserutils.dm.DmnFreelingUtils;

public class DecisionHandler {
	private LinkedHashMap<String, Activity> activitiesList;
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;
	private LinkedHashMap<String, InputData_Dmn> inputDataList;
	private Functions functions;
	private ArrayList<String> decisionPatterns;
	private LinkedHashMap<String, Predicate> predicates;

	public DecisionHandler(LinkedHashMap<String, Activity> activitiesList, LinkedHashMap<String, Token> tokens,
			List<Tree> trees, LinkedHashMap<String, Predicate> predicates) throws IOException {
		this.activitiesList = activitiesList;
		this.tokens = tokens;
		this.trees = trees;
		this.predicates = predicates;
		this.inputDataList = new LinkedHashMap<String, InputData_Dmn>();
		this.functions = new Functions(tokens, trees, activitiesList);
		decisionPatterns = new ArrayList<String>();
		decisionPatterns = DmnFreelingUtils.readPatternFile(DmnFilesUrl.DECISION_PATTERN.toString());
	}

	public LinkedHashMap<String, Decision_Dmn> extractDecionsWithListOfVerbs() throws IOException {
		LinkedHashMap<String, Decision_Dmn> decisionList = new LinkedHashMap<String, Decision_Dmn>();
		DmTreesHandler th = new DmTreesHandler(trees, tokens);
		th.refreshTree(activitiesList);
		ArrayList<String> decisionVerbList = DmnFreelingUtils.readPatternFile(DmnFilesUrl.DECISION_VERBLIST.toString());
		System.out.println("--> Extract Decisions");
		String patternStr1 = "/¦ACTION:t.*¦/=actionVerb";
		TregexPattern pattern1 = TregexPattern.compile(patternStr1);
		for (int i = 0; i < trees.size(); i++) {
			TregexMatcher matcher = pattern1.matcher(trees.get(i));
			while (matcher.findNextMatchingNode()) {
				Tree tActionVerb = matcher.getNode("actionVerb");
				if (tActionVerb != null) {
					String actionVerbToken = DmnFreelingUtils.getTokenFromNode(tActionVerb.label().value());
					if (decisionVerbList.contains(tokens.get(actionVerbToken).getLemma())) {
						Decision_Dmn decision = null;
						decision = extractDecisionAndAddToInputDataList(actionVerbToken, trees.get(i));
						if (decision != null) {
							decisionList.put(decision.getDrgElement().getToken(), decision);
							System.out.println(
									"> Decision: " + decision.getId() + " - " + decision.getDrgElement().getName());
						}
					}
				}
			}
		}
		return decisionList;
	}

	private Decision_Dmn extractDecisionAndAddToInputDataList(String decisionVerbToken, Tree tree) throws IOException {
		InputDataHandler idh = new InputDataHandler(activitiesList, tokens, trees);
		Decision_Dmn decision = null;
		String decisionToken = null;
		for (String patternStr : decisionPatterns) {
			if (decisionVerbToken != null)
				patternStr = patternStr.replace("[decisionVerb]",
						tokens.get(decisionVerbToken).getLemma() + "¦*.*¦ACTION:t.*");
			// patternStr = patternStr.replace("[decisionVerb]","ACTION:t.*");
			TregexPattern pattern = TregexPattern.compile(patternStr);
			TregexMatcher matcher = pattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree tDecisionAction = matcher.getNode("decisionAction");
				Tree tInputData = matcher.getNode("inputData");
				Tree tInputAction = matcher.getNode("inputAction");
				String decisionActionToken = null;
				if (tDecisionAction != null) {
					decisionActionToken = DmnFreelingUtils.getTokenFromNode(tDecisionAction.label().value());
					if (activitiesList.get(decisionActionToken).getAgent() != null) {
						if (tokens.get(activitiesList.get(decisionActionToken).getAgent().getId()).getFunction()
								.equals("SBJ")) {
							decisionToken = activitiesList.get(decisionActionToken).getAgent().getId();
						}
					}
					if (activitiesList.get(decisionActionToken).getPatient() != null && decisionToken == null) {
						// TODO is it redundant!?
						if (tokens.get(decisionActionToken).getTag().equals("VBP")) { // A0 found
							if (activitiesList.get(decisionActionToken).getAgent() != null) {
								decisionToken = activitiesList.get(decisionActionToken).getAgent().getId();
								System.out.println("VBP >> A0 found " + decisionActionToken);
							} else {
								decisionToken = activitiesList.get(decisionActionToken).getPatient().getId();
							}
							// TODO
							// consider "of"?
						} else
							decisionToken = activitiesList.get(decisionActionToken).getPatient().getId();
					}
					if (decisionToken != null) {
						String name = functions.getInputDataName(decisionToken);
						if (name == null)
							break;

						DrgElement drgElement = new DrgElement(decisionToken, name, ActionType.DECISION);
						decision = new Decision_Dmn(decisionToken, drgElement,
								activitiesList.get(decisionActionToken).getAction());
						String inputToken = null;
						String inputDataActionToken = null;
						if (tInputData != null) {
							inputToken = DmnFreelingUtils.getTokenFromNode(tInputData.label().value());
							inputDataActionToken = decisionActionToken;
						} else if (tInputAction != null) {
							inputDataActionToken = DmnFreelingUtils.getTokenFromNode(tInputAction.label().value());
							// TODO: A2?
							/*Predicate predicate = predicates.get(inputDataActionToken);
							Boolean a2Found = false;
							for (Entry<String, PredicateArgument> argument : predicate.getA1PlusArguments()
									.entrySet()) {
								if (DmnFreelingUtils.argumentDifferentToA1(predicate, argument, tokens)) {
									String inputDataName = functions.getInputDataName(argument.getKey());
									if (inputDataName != null) {
										inputToken = argument.getKey();
										a2Found = true;
									}
									break;
								}
							}*/
							////////////////
							/*if (!a2Found)*/
								inputToken = DmnFreelingUtils.getLastTokenFromNode(tInputAction.label().value());
						} else {
							// TODO check again, do it something?
							inputToken = DmnFreelingUtils.getLastTokenFromNode(tDecisionAction.label().value());
							inputDataActionToken = decisionActionToken;
						}
						if (inputToken != null && !inputToken.equals("null")) {
							if (decisionVerbToken != null) {
								name = functions.getInputDataName(inputToken);
								if (name != null) {
									InputData_Dmn inputData = new InputData_Dmn(inputToken, decision,
											new DrgElement(inputToken, name, ActionType.INPUTDATA),
											activitiesList.get(inputDataActionToken).getAction());
									System.out.println("> InputData: " + inputData.getId() + " - "
											+ inputData.getDrgElement().getName());

									inputDataList.put(inputToken+"_"+decisionToken, inputData);

									inputDataList.putAll(idh.getInputDataFromUnderInputData(inputToken, tree, decision,
											decisionActionToken));
								}
							}
						} else if (inputDataActionToken != null) {
							name = tokens.get(inputDataActionToken).getForm();
							InputData_Dmn inputData = new InputData_Dmn(inputDataActionToken, decision,
									new DrgElement(inputDataActionToken, name, ActionType.INPUTDATA),
									activitiesList.get(inputDataActionToken).getAction());
							System.out.println(
									"> InputData: " + inputData.getId() + " - " + inputData.getDrgElement().getName());

							inputDataList.put(inputData.getId(), inputData);

							inputDataList.putAll(idh.getInputDataFromUnderInputData(inputToken, tree, decision,
									decisionActionToken));
						}
					}
				}
			}
		}
		return decision;
	}

	public LinkedHashMap<String, InputData_Dmn> getInputDataList() {
		return inputDataList;
	}

}
