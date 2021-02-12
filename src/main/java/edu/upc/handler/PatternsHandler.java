package edu.upc.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.parser.ParseException;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.upc.entities.Activity;
import edu.upc.entities.Agent;
import edu.upc.entities.Patient;
import edu.upc.entities.Predicate;
import edu.upc.entities.Token;
import edu.upc.freelingutils.ActionType;
import edu.upc.freelingutils.FilesUrl;
import edu.upc.freelingutils.FreelingUtils;

public class PatternsHandler {
	private LinkedHashMap<String, Predicate> predicates;
	private LinkedHashMap<String, Activity> activitiesList;
	private LinkedHashMap<String, Token> tokens;
	private LinkedHashMap<String, Agent> agents;
	private LinkedHashMap<String, Patient> patients;
	private List<Tree> trees;
	private TreesHandler treesHandler;
	private String applyPatterns;
	private String freelingJsonString;

	public PatternsHandler(String text, String applyPatterns, String lang) throws IOException, ParseException {
		this.applyPatterns = applyPatterns;

		ParserHandler ph = new ParserHandler(text);
		this.tokens = ph.getTokens();
		this.trees = ph.getTrees();
		this.activitiesList = ph.getActivitiesList();
		this.predicates = ph.getPredicates();

		treesHandler = new TreesHandler(trees, tokens);
		this.agents = ph.getAgentsList();
		this.patients = ph.getPatientsList();
		this.freelingJsonString = ph.getFreelingJsonString();

		applyAllPatterns();
		makeActivitiesText();
	}

	private void applyAllPatterns() throws IOException {
		TreesHandler th = new TreesHandler(trees, tokens);
		ArrayList<String> patterns = new ArrayList<String>();
		th.refreshTree(activitiesList);

		patterns = FreelingUtils.readPatternFile(FilesUrl.ACTIVITY_PATTERNS_FILE.toString());
		if (isToApplyPattern())
			patternExecutor(patterns, ActionType.ACTIVITY);

	}

	private boolean isToApplyPattern() {
		if (applyPatterns.toLowerCase().equals("all"))
			return true;
		return false;
	}

	private void patternExecutor(ArrayList<String> patternStrList, ActionType role) throws IOException {
		if (role != null)
			System.out.println("--> Identify " + role.toString().toUpperCase() + " and update ActivitiesList");
		for (String patternStr : patternStrList) {
			TregexPattern pattern = TregexPattern.compile(patternStr);
			for (int i = 0; i < trees.size(); i++) {
				TregexMatcher matcher = pattern.matcher(trees.get(i));
				while (matcher.findNextMatchingNode()) {
					Tree tResult = matcher.getNode("result");
					Tree tToRemove = matcher.getNode("toRemove");
					Tree tCondition = matcher.getNode("condition");
					if (tResult != null) {
						String tokenResult = FreelingUtils.getTokenFromNode(tResult.label().value());
						if (activitiesList.containsKey(tokenResult)
								&& activitiesList.get(tokenResult).getRole() == ActionType.ACTION) {
							// if (role != null)
							activitiesList.get(tokenResult).setRole(role);
						}
					}
					if (tToRemove != null) {
						String tokenTremove = FreelingUtils.getTokenFromNode(tToRemove.label().value());
						if (!isUnderCondition(tokenTremove, trees.get(i))) {
							if (activitiesList.containsKey(tokenTremove)) {
								activitiesList.remove(tokenTremove);
							}
						}
					}
					if (tResult != null && tCondition != null) {
						String tokenResult = FreelingUtils.getTokenFromNode(tResult.label().value());
						String conditionToken = FreelingUtils.getTokenFromNode(tCondition.label().value());
						activitiesList.get(tokenResult).setPattern(tokens.get(conditionToken).getLemma());
					}
				}
			}
			treesHandler.refreshTree(activitiesList);
		}

	}

	private boolean isUnderCondition(String tokenTremove, Tree tree) {
		ArrayList<String> patternStrList = new ArrayList<String>();
		// patternStrList.add("/¦" + tokenTremove + "¦/=result >>
		// /¦if¦|¦whether¦|¦either¦|¦can¦/");
		patternStrList.add("/¦" + tokenTremove + "¦/=result >> /¦if¦/");
		patternStrList.add("/¦" + tokenTremove + "¦/=result >> (/¦case¦/ > /¦in¦/)");
		patternStrList.add("/¦" + tokenTremove + "¦/=result >> /¦in_case¦/");
		for (String patternStr : patternStrList) {
			TregexPattern pattern = TregexPattern.compile(patternStr);
			TregexMatcher matcher = pattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree tResult = matcher.getNode("result");
				if (tResult != null)
					return true;
			}
		}
		return false;
	}

	private void makeActivitiesText() {
		for (Entry<String, Activity> activity : activitiesList.entrySet()) {
			if (activity.getValue().getRole().equals(ActionType.ACTIVITY)) {
				Integer begin = activity.getValue().getAction().getBegin();
				Integer end = activity.getValue().getAction().getEnd();
				String text = activity.getValue().getAction().getWord() + " ";
				if (activity.getValue().getPatient() != null) {
					text += activity.getValue().getPatient().getShortText();
				}
				activitiesList.get(activity.getKey()).setBegin(begin);
				activitiesList.get(activity.getKey()).setEnd(end);
				activitiesList.get(activity.getKey()).setText(text.trim());
			} else if (activity.getValue().getRole().equals(ActionType.EVENT)) {
				Integer begin = activity.getValue().getAction().getBegin();
				Integer end = activity.getValue().getAction().getEnd();
				String text = "";
				if (activity.getValue().getPatient() != null) {
					text += activity.getValue().getPatient().getShortText().trim() + " ";
				}
				text += tokens.get(activity.getValue().getId()).getForm();// activity.getValue().getAction().getWord().trim();
				activitiesList.get(activity.getKey()).setBegin(begin);
				activitiesList.get(activity.getKey()).setEnd(end);
				activitiesList.get(activity.getKey()).setText(text.trim());
			}
		}

	}

	public LinkedHashMap<String, Activity> getActivitiesList() {
		return activitiesList;
	}

	public String getApplyPatterns() {
		return applyPatterns;
	}

	public void setApplyPatterns(String applyPatterns) {
		this.applyPatterns = applyPatterns;
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

	public LinkedHashMap<String, Agent> getAgents() {
		return agents;
	}

	public void setAgents(LinkedHashMap<String, Agent> agents) {
		this.agents = agents;
	}

	public String getFreelingJsonString() {
		return freelingJsonString;
	}

	public void setFreelingJsonString(String freelingJsonString) {
		this.freelingJsonString = freelingJsonString;
	}

	public LinkedHashMap<String, Patient> getPatients() {
		return patients;
	}

	public void setPatients(LinkedHashMap<String, Patient> patients) {
		this.patients = patients;
	}

	public LinkedHashMap<String, Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(LinkedHashMap<String, Predicate> predicates) {
		this.predicates = predicates;
	}

}
