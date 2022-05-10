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
import edu.upc.entities.Token;
import edu.upc.entities.dm.DecisionTable_Dmn;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.DrgElement;
import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.entities.dm.InputEntry_Rule;
import edu.upc.parserutils.ActionType;
import edu.upc.parserutils.dm.DmnFilesUrl;
import edu.upc.parserutils.dm.DmnFreelingUtils;

public class DecisionWithoutRequimentLevelHandler {
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;
	private LinkedHashMap<String, Activity> activitiesList;
	private Functions functions;
	private LinkedHashMap<String, InputData_Dmn> inputDataList;
	private LinkedHashMap<String, Decision_Dmn> decisionList;

	private InputEntryHandler ieh;

	public DecisionWithoutRequimentLevelHandler(LinkedHashMap<String, Activity> activitiesList,
			LinkedHashMap<String, Token> tokens, List<Tree> trees, LinkedHashMap<String, Predicate> predicates,
			LinkedHashMap<String, DecisionTable_Dmn> decisionTableList) throws IOException {
		this.activitiesList = activitiesList;
		this.tokens = tokens;
		this.trees = trees;

		functions = new Functions(tokens, trees, activitiesList);

		ieh = new InputEntryHandler(activitiesList, tokens, trees, predicates);

		inputDataList = new LinkedHashMap<String, InputData_Dmn>();
		decisionList = new LinkedHashMap<String, Decision_Dmn>();
	}

	public void process() throws IOException {
		// Remove weak actions
		activitiesList = DmnFreelingUtils.removeActions(trees, tokens, activitiesList);

		ArrayList<String> patternStrList = DmnFreelingUtils.readPatternFile(DmnFilesUrl.DECISIONTABLE_RULES.toString());
		LinkedHashMap<String, InputEntry_Rule> inputEntries = new LinkedHashMap<String, InputEntry_Rule>();

		for (String patternStr : patternStrList) {
			TregexPattern pattern = TregexPattern.compile(patternStr);
			for (int i = 0; i < trees.size(); i++) {
				TregexMatcher matcher = pattern.matcher(trees.get(i));
				while (matcher.findNextMatchingNode()) {
					Tree tDecisionAction = matcher.getNode("decisionAction");
					Tree tDecisionNoun = matcher.getNode("decisionNoun");
					Tree tInputAction = matcher.getNode("inputAction");
					Tree tInputNoun = matcher.getNode("inputNoun");
					Decision_Dmn decision = null;
					String decisionActionToken = null;
					String decisionNounToken = null;
					if (tDecisionAction != null) {
						decisionActionToken = DmnFreelingUtils.getTokenFromNode(tDecisionAction.label().value());
						decision = createDecision(decisionActionToken);
						if (decision != null) {
							inputEntries = ieh.getInputEntries(tInputAction, tInputNoun, trees.get(i), null);
						}
					} else if (tDecisionNoun != null) {
						decisionNounToken = DmnFreelingUtils.getTokenFromNode(tDecisionNoun.label().value());
						String name = functions.getEntryName(decisionNounToken);
						DrgElement drgElement = new DrgElement(decisionNounToken, name, ActionType.DECISION);
						decision = new Decision_Dmn(decisionNounToken, drgElement, null);
						if (decision != null) {
							inputEntries = ieh.getInputEntries(tInputAction, tInputNoun, trees.get(i), null);
						}
					} else
						break;
					if (inputEntries.size() > 0) {
						decision = getUniqDecision(decision);
						decisionList.put(decision.getId(), decision);
						System.out.println(
								"> Decision: " + decision.getId() + " - " + decision.getDrgElement().getName());
						for (Entry<String, InputEntry_Rule> inputEntry : inputEntries.entrySet()) {
							if (inputEntry.getValue().getInput() != null) {
								String inputDataToken = inputEntry.getValue().getInput().getId();
								String inputDataName = inputEntry.getValue().getInput().getName();
								InputData_Dmn inputData = new InputData_Dmn(inputDataToken, decision,
										new DrgElement(inputDataToken, inputDataName, ActionType.INPUTDATA), null);
								inputDataList.put(inputData.getId(), inputData);
								System.out.println(">> InputData: " + inputData.getId() + " - "
										+ inputData.getDrgElement().getName());
							}
						}
					}
				}
			}
		}
	}

	private Decision_Dmn getUniqDecision(Decision_Dmn decision) {
		for (Entry<String, Decision_Dmn> decision_Dmn : decisionList.entrySet()) {
			if (decision.getDrgElement().getName().equals(decision_Dmn.getValue().getDrgElement().getName()))
				return decision_Dmn.getValue();
		}
		return decision;
	}

	private Decision_Dmn createDecision(String decisionActionToken) {
		String decisionToken = null;
		Decision_Dmn decision = null;
		if (activitiesList.get(decisionActionToken).getAgent() != null) {
			decisionToken = activitiesList.get(decisionActionToken).getAgent().getId();
		}
		if (activitiesList.get(decisionActionToken).getPatient() != null && decisionToken == null) {
			//if (tokens.get(activitiesList.get(decisionActionToken).getPatient().getId()).getFunction().equals("SBJ")) {
				decisionToken = activitiesList.get(decisionActionToken).getPatient().getId();
			//}
		}

		if (decisionToken != null) {
			String name = functions.getInputDataName(decisionToken);
			if (name == null && tokens.get(decisionToken).getPos().equals("pronoun")) {
					decisionToken = activitiesList.get(decisionActionToken).getPatient().getId();
					name = functions.getInputDataName(decisionToken);
			}
			if (name == null) 
				name = tokens.get(decisionActionToken).getLemma();
			
			DrgElement drgElement = new DrgElement(decisionToken, name, ActionType.DECISION);
			decision = new Decision_Dmn(decisionToken, drgElement, activitiesList.get(decisionActionToken).getAction());
		} else {
			// find one before the last
			String name = activitiesList.get(decisionActionToken).getAction().getWord();
			DrgElement drgElement = new DrgElement(decisionActionToken, name, ActionType.DECISION);
			decision = new Decision_Dmn(decisionActionToken, drgElement,
					activitiesList.get(decisionActionToken).getAction());
		}
		return decision;
	}

	public LinkedHashMap<String, InputData_Dmn> getInputDataList() {
		return inputDataList;
	}

	public void setInputDataList(LinkedHashMap<String, InputData_Dmn> inputDataList) {
		this.inputDataList = inputDataList;
	}

	public LinkedHashMap<String, Decision_Dmn> getDecisionList() {
		return decisionList;
	}

	public void setDecisionList(LinkedHashMap<String, Decision_Dmn> decisionList) {
		this.decisionList = decisionList;
	}

}
