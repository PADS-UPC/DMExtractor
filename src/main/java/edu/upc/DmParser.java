package edu.upc;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.parser.ParseException;

import edu.stanford.nlp.trees.Tree;
import edu.upc.entities.Activity;
import edu.upc.entities.Token;
import edu.upc.entities.dm.DecisionTable_Dmn;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.entities.dm.InputEntry_Rule;
import edu.upc.entities.dm.Input_Dmn;
import edu.upc.entities.dm.Requirement;
import edu.upc.entities.dm.Rule_Table;
import edu.upc.freelingutils.ActionType;
import edu.upc.handler.LangHandler;
import edu.upc.handler.PatternsHandler;
import edu.upc.handler.dm.DmPatternsHandler;
import edu.upc.handler.dm.DmTreesHandler;

public class DmParser {
	private LinkedHashMap<String, Activity> dmnActivitiesList;
	private LinkedHashMap<String, Decision_Dmn> decisionList;
	private LinkedHashMap<String, InputData_Dmn> inputDataList;
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;
	private LinkedHashMap<String, DecisionTable_Dmn> decisionTableList;

	private LinkedHashMap<String, Activity> activitiesList;
	private LinkedHashMap<String, Requirement> requirementList;

	public DmParser(String text, String applyPattern, String lang) throws ParseException, IOException {
		LangHandler.changeFoldersPath(lang);

		PatternsHandler ph = new PatternsHandler(text, applyPattern, lang);
		this.activitiesList = ph.getActivitiesList();
		this.tokens = ph.getTokens();
		this.trees = ph.getTrees();

		// copy actions from activity list
		dmnActivitiesList = new LinkedHashMap<String, Activity>();
		for (Entry<String, Activity> activity : activitiesList.entrySet()) {
			Activity Activity = new Activity(activity.getKey(), activity.getValue().getAction());
			Activity.setRole(ActionType.ACTION);
			Activity.setText(activity.getValue().getText());
			Activity.setAgent(activity.getValue().getAgent());
			Activity.setPatient(activity.getValue().getPatient());
			Activity.setBegin(activity.getValue().getBegin());
			Activity.setEnd(activity.getValue().getEnd());
			dmnActivitiesList.put(activity.getKey(), Activity);
		}
		DmTreesHandler treesHandler = new DmTreesHandler(trees, tokens);
		treesHandler.refreshTree(dmnActivitiesList);

		DmPatternsHandler dmnPH = new DmPatternsHandler(ph.getPredicates(), dmnActivitiesList, tokens, trees, ph);

		this.decisionList = dmnPH.getDecisionList();
		this.inputDataList = dmnPH.getInputList();

		decisionTableList = dmnPH.getDecisionTableList();
		requirementList = dmnPH.getRequirementList();

		treesHandler = new DmTreesHandler(trees, tokens);
		treesHandler.refreshTree(dmnActivitiesList);
		treesHandler.refreshTreeWithDecisions(decisionList);
		treesHandler.refreshTreeWithInputs(inputDataList);

		System.out.println("---------------------DECISIONS-------------------------");
		for (Entry<String, Decision_Dmn> decision : dmnPH.getDecisionList().entrySet()) {
			System.out.println(decision.getKey() + " - " + decision.getValue().getDrgElement().getName());
		}
		System.out.println("---------------------REQUIREMENTS-------------------------");
		System.out.println(getRequirementsText());
		System.out.println("--------------------------------------------------");

		System.out.println("--------- RULES -------------------");
		if (decisionTableList != null)
			for (Entry<String, DecisionTable_Dmn> decisionTable : decisionTableList.entrySet()) {
				System.out.println("TABLE: " + decisionTable.getKey() + " - " + decisionTable.getValue().getName());
				System.out.print("INPUTS: ");
				for (Entry<String, Input_Dmn> input : decisionTable.getValue().getInputs().entrySet()) {
					System.out.print("> " + input.getValue().getName() + "\t");
				}
				System.out.print("\n");
				if (decisionTable.getValue().getRules() != null) {
					for (Rule_Table rule : decisionTable.getValue().getRules()) {
						for (Entry<String, InputEntry_Rule> inputEntry : rule.getInputEntries().entrySet()) {
							if (inputEntry.getValue().getNegation())
								System.out.print("> not(" + inputEntry.getValue().getText() + ")\t");
							else
								System.out.print("> " + inputEntry.getValue().getText() + "\t");
						}
						System.out.print("> " + rule.getOutputEntry().getText() + "\n");
					}
				}
			}
		System.out.println("DONE!");
	}

	private String getRequirementsText() {
		String text = "";
		text += "InputData -->  Decision\n";
		for (Entry<String, Requirement> requirement : requirementList.entrySet()) {
			text += requirement.getValue().getInputData().getDrgElement().getToken() + " - ";
			text += requirement.getValue().getInputData().getDrgElement().getName() + "("
					+ requirement.getValue().getInputData().getDrgElement().getType() + ")";
			text += " --> ";
			text += requirement.getValue().getDecision().getId() + " - ";
			text += requirement.getValue().getDecision().getDrgElement().getName() + "("
					+ requirement.getValue().getDecision().getDrgElement().getType() + ")" + "\n";
		}
		return text;
	}

	public LinkedHashMap<String, Token> getTokens() {
		return tokens;
	}

	public void setTokens(LinkedHashMap<String, Token> tokens) {
		this.tokens = tokens;
	}

	public List<Tree> getTrees() {
		return trees;
	}

	public void setTrees(List<Tree> trees) {
		this.trees = trees;
	}

	public LinkedHashMap<String, Decision_Dmn> getDecisionList() {
		return decisionList;
	}

	public void setDecisionList(LinkedHashMap<String, Decision_Dmn> decisionList) {
		this.decisionList = decisionList;
	}

	public LinkedHashMap<String, DecisionTable_Dmn> getDecisionTableList() {
		return decisionTableList;
	}

	public void setDecisionTableList(LinkedHashMap<String, DecisionTable_Dmn> decisionTableList) {
		this.decisionTableList = decisionTableList;
	}

	public LinkedHashMap<String, Requirement> getRequirementList() {
		return requirementList;
	}

	public void setRequirementList(LinkedHashMap<String, Requirement> requirementList) {
		this.requirementList = requirementList;
	}

	public LinkedHashMap<String, InputData_Dmn> getInputDataList() {
		return inputDataList;
	}

	public void setInputDataList(LinkedHashMap<String, InputData_Dmn> inputDataList) {
		this.inputDataList = inputDataList;
	}

}
