package edu.upc.handler.dm;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.parser.ParseException;

import edu.stanford.nlp.trees.Tree;
import edu.upc.entities.Activity;
import edu.upc.entities.Predicate;
import edu.upc.entities.Token;
import edu.upc.entities.dm.DecisionTable_Dmn;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.entities.dm.Requirement;
import edu.upc.handler.PatternsHandler;

public class DmPatternsHandler {
	private LinkedHashMap<String, Predicate> predicates;
	private LinkedHashMap<String, Activity> activitiesList;
	private LinkedHashMap<String, Decision_Dmn> decisionList;
	private LinkedHashMap<String, InputData_Dmn> inputDataList;
	private LinkedHashMap<String, Requirement> requirementList;
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;
	private DmTreesHandler treesHandler;
	private LinkedHashMap<String, DecisionTable_Dmn> decisionTableList;

	public DmPatternsHandler(LinkedHashMap<String, Predicate> predicates,
			LinkedHashMap<String, Activity> activitiesList, LinkedHashMap<String, Token> tokens, List<Tree> trees,
			PatternsHandler ph) throws IOException, ParseException {

		this.tokens = tokens;
		this.trees = trees;
		this.activitiesList = activitiesList;
		this.predicates = predicates;

		decisionList = new LinkedHashMap<String, Decision_Dmn>();
		inputDataList = new LinkedHashMap<String, InputData_Dmn>();
		requirementList = new LinkedHashMap<String, Requirement>();

		// DmnNlpParserHandler nlpParserHandler = new DmnNlpParserHandler(text);

		// -------------------------------------------------------
		// ------------ Extract Decision and InputDatas ----------
		// -------------------------------------------------------
		DecisionHandler dh = new DecisionHandler(activitiesList, tokens, trees, predicates);
		decisionList = dh.extractDecionsWithListOfVerbs();
		inputDataList = dh.getInputDataList();

		refreshTrees();

		// -------------------------------------------------------
		// ----------------- Make requirements ----------------
		// -------------------------------------------------------
		RequirementHandler rh = new RequirementHandler();
		requirementList = rh.process(inputDataList);

		//
		decisionList = removeRepeatedDecision();
		//
		
		// ---------------------------------------------------------------
		// --------- Extract DecisionTables within each sentence ---------
		// ---------------------------------------------------------------
		decisionTableList = new LinkedHashMap<String, DecisionTable_Dmn>();
		DecisionTableHandler dtHadler = new DecisionTableHandler(requirementList);
		decisionTableList = dtHadler.process(decisionList);

		// ---------------------------------------------------------------
		// ------------------------- Extract Rules -----------------------
		// ---------------------------------------------------------------
		RulesHandler rulesHandler = new RulesHandler(activitiesList, tokens, trees, predicates, inputDataList,
				decisionList, decisionTableList);
		decisionTableList = rulesHandler.ExtractRules();

	}

	private LinkedHashMap<String, Decision_Dmn> removeRepeatedDecision() {
		// Remove repeated decisions
		LinkedHashMap<String, Decision_Dmn> decisionListTmp = new LinkedHashMap<String, Decision_Dmn>();
		for (Entry<String, Requirement> requiriment : requirementList.entrySet()) {
			Boolean found = false;
			for (Entry<String, Decision_Dmn> decisionTmp : decisionListTmp.entrySet()) {
				if (decisionTmp.getValue().getDrgElement().getName()
						.equals(requiriment.getValue().getDecision().getDrgElement().getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				decisionListTmp.put(requiriment.getValue().getDecision().getId(), requiriment.getValue().getDecision());
			}
		}
		return decisionListTmp;
	}

	private void refreshTrees() throws IOException {
		treesHandler = new DmTreesHandler(trees, tokens);
		treesHandler.refreshTree(activitiesList);
		treesHandler.refreshTreeWithDecisions(decisionList);
		treesHandler.refreshTreeWithInputs(inputDataList);
	}

	public LinkedHashMap<String, Activity> getActivitiesList() {
		return activitiesList;
	}

	public List<Tree> getTrees() {
		return trees;
	}

	public void setTrees(List<Tree> trees) {
		this.trees = trees;
	}

	public LinkedHashMap<String, Token> getTokens() {
		return tokens;
	}

	public void setTokens(LinkedHashMap<String, Token> tokens) {
		this.tokens = tokens;
	}

	public LinkedHashMap<String, Decision_Dmn> getDecisionList() {
		return decisionList;
	}

	public void setDecisionList(LinkedHashMap<String, Decision_Dmn> decisionList) {
		this.decisionList = decisionList;
	}

	public LinkedHashMap<String, InputData_Dmn> getInputList() {
		return inputDataList;
	}

	public void setInputList(LinkedHashMap<String, InputData_Dmn> inputList) {
		this.inputDataList = inputList;
	}

	public LinkedHashMap<String, InputData_Dmn> getInputDataList() {
		return inputDataList;
	}

	public void setInputDataList(LinkedHashMap<String, InputData_Dmn> inputDataList) {
		this.inputDataList = inputDataList;
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

	public LinkedHashMap<String, Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(LinkedHashMap<String, Predicate> predicates) {
		this.predicates = predicates;
	}

}
