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
import edu.upc.entities.Agent;
import edu.upc.entities.Predicate;
import edu.upc.entities.Token;
import edu.upc.entities.dm.DecisionTable_Dmn;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.DrgElement;
import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.entities.dm.InputEntry_Rule;
import edu.upc.entities.dm.Input_Dmn;
import edu.upc.entities.dm.OutputEntry_Rule;
import edu.upc.entities.dm.Rule_Table;
import edu.upc.parserutils.ActionType;
import edu.upc.parserutils.dm.DmnFilesUrl;
import edu.upc.parserutils.dm.DmnFreelingUtils;
import edu.upc.parserutils.dm.TypeRef_Table;

public class RulesHandler {
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;
	private LinkedHashMap<String, Activity> activitiesList;
	private Functions functions;
	private InputEntryHandler ieh;
	private OutputEntryHandler oeh;
	private LinkedHashMap<String, InputData_Dmn> inputDataList;
	private LinkedHashMap<String, DecisionTable_Dmn> decisionTableList;

	public RulesHandler(LinkedHashMap<String, Activity> activitiesList, LinkedHashMap<String, Token> tokens,
			List<Tree> trees, LinkedHashMap<String, Predicate> predicates,
			LinkedHashMap<String, InputData_Dmn> inputDataList, LinkedHashMap<String, Decision_Dmn> decisionList,
			LinkedHashMap<String, DecisionTable_Dmn> decisionTableList) throws IOException {
		this.activitiesList = activitiesList;
		this.tokens = tokens;
		this.trees = trees;
		this.inputDataList = inputDataList;
		this.decisionTableList = decisionTableList;

		functions = new Functions(tokens, trees, activitiesList);
		ieh = new InputEntryHandler(activitiesList, tokens, trees, predicates);
		oeh = new OutputEntryHandler(activitiesList, predicates, tokens, trees);
	}

	public LinkedHashMap<String, DecisionTable_Dmn> ExtractRules() throws IOException {
		// Remove weak actions
		activitiesList = DmnFreelingUtils.removeActions(trees, tokens, activitiesList);

		ArrayList<String> patternStrList = DmnFreelingUtils.readPatternFile(DmnFilesUrl.DECISIONTABLE_RULES.toString());
		LinkedHashMap<String, InputEntry_Rule> inputEntries = new LinkedHashMap<String, InputEntry_Rule>();

		DecisionTable_Dmn decisionTable = null;
		if (decisionTableList.size() == 0)
			return null;

		for (String patternStr : patternStrList) {
			TregexPattern pattern = TregexPattern.compile(patternStr);
			for (int i = 0; i < trees.size(); i++) {
				TregexMatcher matcher = pattern.matcher(trees.get(i));
				while (matcher.findNextMatchingNode()) {
					Tree tDecisionAction = matcher.getNode("decisionAction");
					Tree tDecisionNoun = matcher.getNode("decisionNoun");
					Tree tInputAction = matcher.getNode("inputAction");
					Tree tInputNoun = matcher.getNode("inputNoun");
					Tree tNegation = matcher.getNode("negation");
					Tree tCondition = matcher.getNode("condition");
					Decision_Dmn decision = null;
					String decisionActionToken = null;
					String decisionNounToken = null;
					if (tDecisionAction != null) {
						decisionActionToken = DmnFreelingUtils.getTokenFromNode(tDecisionAction.label().value());
						decision = createDecision(decisionActionToken);
						if (decision != null) {
							inputEntries = ieh.getInputEntries(tInputAction, tInputNoun, trees.get(i), tNegation);
						}
					} else if (tDecisionNoun != null) {
						decisionNounToken = DmnFreelingUtils.getTokenFromNode(tDecisionNoun.label().value());
						String name = functions.getEntryName(decisionNounToken);
						DrgElement drgElement = new DrgElement(decisionNounToken, name, ActionType.DECISION);
						decision = new Decision_Dmn(decisionNounToken, drgElement, null);
						if (decision != null) {
							inputEntries = ieh.getInputEntries(tInputAction, tInputNoun, trees.get(i), tNegation);
						}
					} else
						break;
					if (inputEntries.size() > 0) {
						inputEntries = removeRepeatedInputHeader(inputEntries);
						decisionTable = findDecisionTableWithName(decision);
						inputEntries = fillNullInputs(inputEntries, tCondition, decisionTable);

						if (decisionTable != null) {
							OutputEntry_Rule outputEntry = oeh.getOutputEntryRole(decisionTable, decisionActionToken,
									null);
							Rule_Table rule = new Rule_Table(inputEntries, outputEntry);
							if (decisionTable.getRules() == null)
								decisionTable.setRules(new ArrayList<Rule_Table>());
							decisionTable.getRules().add(rule);
							inputEntries = new LinkedHashMap<String, InputEntry_Rule>();
						} else if (inputEntries != null) {
							decisionTable = decisionTableList.entrySet().iterator().next().getValue();
							// Find Input name inside Table
							Boolean found = false;
							DecisionTable_Dmn decisionTableTmp = null;
							for (Entry<String, InputEntry_Rule> inputEntry : inputEntries.entrySet()) {
								if (inputEntry.getValue().getInput() != null) {
									for (Entry<String, InputData_Dmn> inputData : inputDataList.entrySet()) {
										if (inputData.getValue().getDrgElement().getName()
												.contains(inputEntry.getValue().getInput().getName())) {

											decisionTable = findDecisionTableWithName(
													inputData.getValue().getDecision());
											if (decisionTable != null) {
												Integer count = 0;
												for (Entry<String, Input_Dmn> inputTmp : decisionTable.getInputs()
														.entrySet()) {
													for (Entry<String, InputEntry_Rule> inputEntryTmp : inputEntries
															.entrySet()) {
														if (inputEntryTmp.getValue().getInput() != null) {
															if (inputTmp.getValue().getName().contains(
																	inputEntryTmp.getValue().getInput().getName()))
																count++;
														}
													}
												}
												if (count == decisionTable.getInputs().size()) {
													// if (count > 0) {
													addRuleToDecisionTable(decisionTable, decisionActionToken,
															decisionNounToken, inputEntries);
													inputEntries = new LinkedHashMap<String, InputEntry_Rule>();
													found = true;
													break;
												} else if (count > 0 && count < decisionTable.getInputs().size()) {
													decisionTableTmp = decisionTable;
												}
											}
										} else if (inputEntry.getValue().getText() != null) {
											if (inputData.getValue().getDrgElement().getName()
													.contains(inputEntry.getValue().getText())) {
												String name = inputEntry.getValue().getText();
												Input_Dmn input = new Input_Dmn(inputEntry.getKey(), name,
														name.replaceAll("[^A-Za-z0-9]", "").toLowerCase(),
														TypeRef_Table.BOOLEAN);
												inputEntry.getValue().setInput(input);
												inputEntry.getValue().setText("true");

												decisionTable = findDecisionTableWithName(
														inputData.getValue().getDecision());
												addRuleToDecisionTable(decisionTable, decisionActionToken,
														decisionNounToken, inputEntries);

												inputEntries = new LinkedHashMap<String, InputEntry_Rule>();
												found = true;
												break;
											}
										}
									}
								}
								if (found)
									break;
							}
							if (!found && decisionTableTmp != null) {
								decisionTable = decisionTableTmp;
								addRuleToDecisionTable(decisionTable, decisionActionToken, decisionNounToken,
										inputEntries);
							} else if (!found && decisionTableTmp == null) {
								System.out.println(">> Table = null");
							}
						}
					}
				}
			}
		}
		return decisionTableList;
	}

	private LinkedHashMap<String, InputEntry_Rule> fillNullInputs(LinkedHashMap<String, InputEntry_Rule> inputEntries,
			Tree tCondition, DecisionTable_Dmn decisionTable) {
		for (Entry<String, InputEntry_Rule> inputEntry : inputEntries.entrySet()) {
			if (inputEntry.getValue().getInput() == null) {
				if (inputEntries.size() == 1 && decisionTable != null) {
					Input_Dmn input = decisionTable.getInputs().entrySet().iterator().next().getValue();
					functions.getEntryName(inputEntry.getKey());
					input.setTypeRef(functions.getType());
					inputEntries.get(inputEntry.getKey()).setInput(input);
				} else if (tCondition != null) {
					Activity activit = DmnFreelingUtils.findFirstActivity(tCondition, activitiesList);
					Agent agent = activit.getAgent();
					String agentToken = null;
					String agentName = null;
					if (agent != null) {
						agentToken = agent.getId();
						agentName = functions.getEntryName(agentToken);
					} else {
						if (activit.getPatient() != null) {
							if (tokens.get(activit.getPatient().getId()).getFunction().equals("SBJ")) {
								agentToken = activit.getPatient().getId();
								agentName = functions.getEntryName(agentToken);
							}
						}
					}

					Input_Dmn input = null;
					if (agentName != null) {
						input = new Input_Dmn(agentToken, agentName,
								agentName.replaceAll("[^A-Za-z0-9]", "").toLowerCase(), functions.getType());
						functions.getEntryName(inputEntry.getKey());
						input.setTypeRef(functions.getType());
						inputEntry.getValue().setInput(input);
					}
				}
			}
		}

		return inputEntries;
	}

	private void addRuleToDecisionTable(DecisionTable_Dmn decisionTable, String decisionActionToken,
			String decisionNounToken, LinkedHashMap<String, InputEntry_Rule> inputEntries) {
		OutputEntry_Rule outputEntry = oeh.getOutputEntryRole(decisionTable, decisionActionToken, decisionNounToken);
		Rule_Table rule = new Rule_Table(inputEntries, outputEntry);
		if (decisionTable.getRules() == null)
			decisionTable.setRules(new ArrayList<Rule_Table>());
		decisionTable.getRules().add(rule);
	}

	private DecisionTable_Dmn findDecisionTableWithName(Decision_Dmn decision) {
		if (decision != null)
			for (Entry<String, DecisionTable_Dmn> decisionTable : decisionTableList.entrySet()) {
				if (decisionTable.getValue().getName().contains(decision.getDrgElement().getName())
						|| decision.getDrgElement().getName().contains(decisionTable.getValue().getName())) {
					return decisionTable.getValue();
				}
			}
		return null;
	}

	private LinkedHashMap<String, InputEntry_Rule> removeRepeatedInputHeader(
			LinkedHashMap<String, InputEntry_Rule> inputEntries) {
		LinkedHashMap<String, InputEntry_Rule> inputEntriesTmp = null;
		for (Entry<String, InputEntry_Rule> inputEntry : inputEntries.entrySet()) {
			if (inputEntry.getValue().getInput() != null) {
				if (inputEntriesTmp == null) {
					inputEntriesTmp = new LinkedHashMap<String, InputEntry_Rule>();
					inputEntriesTmp.put(inputEntry.getKey(), inputEntry.getValue());
				}
				for (Entry<String, InputEntry_Rule> inputEntryTmp : inputEntriesTmp.entrySet()) {
					if (!inputEntry.getValue().getInput().getName()
							.equals(inputEntryTmp.getValue().getInput().getName())) {
						inputEntriesTmp.put(inputEntry.getKey(), inputEntry.getValue());
						break;
					}
				}
			} else {
				if (inputEntriesTmp == null)
					inputEntriesTmp = new LinkedHashMap<String, InputEntry_Rule>();
				inputEntriesTmp.put(inputEntry.getKey(), inputEntry.getValue());
			}
		}
		return inputEntriesTmp;
	}

	private Decision_Dmn createDecision(String decisionActionToken) {
		String decisionToken = null;
		Decision_Dmn decision = null;
		if (activitiesList.get(decisionActionToken).getAgent() != null) {
			decisionToken = activitiesList.get(decisionActionToken).getAgent().getId();
		}
		if (activitiesList.get(decisionActionToken).getPatient() != null && decisionToken == null) {
			if (tokens.get(activitiesList.get(decisionActionToken).getPatient().getId()).getFunction().equals("SBJ")) {
				decisionToken = activitiesList.get(decisionActionToken).getPatient().getId();
			}
		}

		if (decisionToken != null) {
			String name = functions.getInputDataName(decisionToken);
			// Necessary?---
			if (name == null)
				name = tokens.get(decisionActionToken).getLemma();
			// if (!agentFound && activitiesList.get(decisionActionToken).getPatient() !=
			// null)
			// name = tokens.get(decisionActionToken).getLemma() + " " + name;
			// else
			// name = name + " " + tokens.get(decisionActionToken).getLemma();
			// -----
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

}
