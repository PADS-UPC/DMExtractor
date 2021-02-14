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
import edu.upc.entities.Patient;
import edu.upc.entities.Predicate;
import edu.upc.entities.PredicateArgument;
import edu.upc.entities.Token;
import edu.upc.entities.dm.InputEntry_Rule;
import edu.upc.entities.dm.Input_Dmn;
import edu.upc.freelingutils.dm.DmnFilesUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;
import edu.upc.freelingutils.dm.TypeRef_Table;

public class InputEntryHandler {
	private LinkedHashMap<String, Activity> activitiesList;
	private LinkedHashMap<String, Token> tokens;
	private LinkedHashMap<String, Predicate> predicates;
	private Functions functions;
	private List<Tree> trees;
	private ArrayList<String> days;
	private ArrayList<String> months;
	private ArrayList<String> years;
	private ArrayList<String> weather;

	public InputEntryHandler(LinkedHashMap<String, Activity> activitiesList, LinkedHashMap<String, Token> tokens,
			List<Tree> trees, LinkedHashMap<String, Predicate> predicates) throws IOException {
		this.activitiesList = activitiesList;
		this.tokens = tokens;
		this.predicates = predicates;
		this.trees = trees;
		functions = new Functions(tokens, trees, activitiesList);
		this.days = DmnFreelingUtils.readPatternFile(DmnFilesUrl.DAYS.toString());
		this.months = DmnFreelingUtils.readPatternFile(DmnFilesUrl.MONTHS.toString());
		this.years = DmnFreelingUtils.readPatternFile(DmnFilesUrl.YEARS.toString());
		this.weather = DmnFreelingUtils.readPatternFile(DmnFilesUrl.WEATHER.toString());

	}

	public LinkedHashMap<String, InputEntry_Rule> getInputEntries(Tree tInputAction, Tree tInputNoun, Tree tree,
			Tree tNegation) throws IOException {
		LinkedHashMap<String, InputEntry_Rule> inputEntries = new LinkedHashMap<String, InputEntry_Rule>();
		if (tInputAction != null) {
			// There is inputs under inputAction
			String actionToken = DmnFreelingUtils.getTokenFromNode(tInputAction.label().value());
			Input_Dmn input = null;
			InputEntry_Rule inputEntry_Rule = getInputEntryFromAction(actionToken, input, tNegation);
			inputEntries.put(inputEntry_Rule.getId(), inputEntry_Rule);
			inputEntries.putAll(getInputEntryUnderAction(actionToken, tree, tNegation));

		} else if (tInputNoun != null) {
			Input_Dmn input = null;
			String resultNounToken = DmnFreelingUtils.getTokenFromNode(tInputNoun.label().value());
			inputEntries.putAll(getInputEntryFromNoun(resultNounToken, input, tNegation));
		}
		return inputEntries;
	}

	private Input_Dmn findInput(String resultActionToken) {
		Input_Dmn input = null;
		Agent agent = activitiesList.get(resultActionToken).getAgent();
		String agentToken = null;
		// Agent (A0) inside condition will be the input (Header of Table)
		if (agent != null) {
			agentToken = agent.getId();

		} else {
			if (activitiesList.get(resultActionToken).getPatient() != null) {
				if (tokens.get(activitiesList.get(resultActionToken).getPatient().getId()).getFunction()
						.equals("SBJ")) {
					agentToken = activitiesList.get(resultActionToken).getPatient().getId();
				}
			}
		}
		if (agentToken != null) {
			String agentName = functions.getInputDataName(agentToken);
			if (agentName != null)
				input = new Input_Dmn(agentToken, agentName, agentName.replaceAll("[^A-Za-z0-9]", "").toLowerCase(),
						functions.getType());
		}
		return input;
	}

	private InputEntry_Rule getInputEntryFromAction(String resultActionToken, Input_Dmn input, Tree tNegation)
			throws IOException {
		Boolean negation = DmnFreelingUtils.isNegation(resultActionToken, trees);
		if (tNegation != null)
			negation = true;
		if (input == null) {
			input = findInput(resultActionToken);
		}
		InputEntry_Rule inputEntry = null;
		// if (input != null) {
		Patient patient = activitiesList.get(resultActionToken).getPatient();
		Predicate predicate = predicates.get(resultActionToken);
		Boolean a2Found = false;
		// Find Agent (A1,A2,AM-LOC) within condition that will be the INPUTENTRY result
		String patientName = null;
		for (Entry<String, PredicateArgument> argument : predicate.getA1PlusArguments().entrySet()) {
			if (DmnFreelingUtils.argumentDifferentToA1(predicate, argument, tokens)) {
				if (input == null)
					input = getInputFromInputEntryNull(argument.getKey());
				if (isNumericVerb(resultActionToken)) {
					patientName = functions.getEntryName(resultActionToken);
				} else
					patientName = functions.getEntryName(argument.getKey());
				if (input != null)
					input.setTypeRef(functions.getType());
				inputEntry = new InputEntry_Rule(argument.getKey(), patientName, input, negation);
				a2Found = true;
				break;
			} else if (predicate.getA1PlusArguments().get(argument.getKey()).getRole().equals("A1")) {
				if (input != null) {
					String agentName = functions.getInputDataName(argument.getKey());
					if (!input.getName().toLowerCase().equals(agentName.toLowerCase())) {
						if (isNumericVerb(resultActionToken)) {
							patientName = functions.getEntryName(resultActionToken);
						} else
							patientName = functions.getEntryName(patient.getId()); // patient.getCompleteText().trim();
																					// //
																					// functions.getTextFromNode(patient.getId());
						// if (input != null)
						input.setTypeRef(functions.getType());
						inputEntry = new InputEntry_Rule(patient.getId(), patientName, input, negation);
						a2Found = true;
						break;
					} else {
						PredicateArgument arg = DmnFreelingUtils.getSecondA1(predicate, argument.getKey(), tokens);
						if (arg != null) {
							negation = DmnFreelingUtils.isNegation(arg.getHead_token(), trees);
							if (tNegation != null)
								negation = true;
							if (isNumericVerb(resultActionToken)) {
								patientName = functions.getEntryName(resultActionToken);
							} else
								patientName = functions.getEntryName(arg.getHead_token());
							if (input != null)
								input.setTypeRef(functions.getType());
							inputEntry = new InputEntry_Rule(arg.getHead_token(), patientName, input, negation);
							a2Found = true;
							break;
						}
					}
				}
			}
		}
		if (!a2Found || patientName == null) {
			String id = tokens.get(resultActionToken).getId();
			patientName = tokens.get(resultActionToken).getLemma();
			if (patientName.equals("be")) {
				patientName = functions.getEntryName(patient.getId());
				if (input != null)
					input.setTypeRef(functions.getType());
			}
			inputEntry = new InputEntry_Rule(id, patientName, input, negation);
		}
		if (input != null) {
			if (input.getName().toLowerCase().trim().contains(inputEntry.getText().toLowerCase().trim())) {
				inputEntry.setText("true");
				input.setTypeRef(TypeRef_Table.BOOLEAN);
			}
		}
		/*
		 * } else { // return the action String name =
		 * tokens.get(resultActionToken).getLemma(); input = null; inputEntry = new
		 * InputEntry_Rule(resultActionToken, name, input, negation); }
		 */
		return inputEntry;
	}

	private boolean isNumericVerb(String resultActionToken) {
		if (tokens.get(resultActionToken).getLemma().equals("begin")
				|| tokens.get(resultActionToken).getLemma().equals("exceed")) {
			return true;
		}
		return false;
	}

	public LinkedHashMap<String, InputEntry_Rule> getInputEntryUnderAction(String mainActionToken, Tree tree,
			Tree tNegation) throws IOException {
		LinkedHashMap<String, InputEntry_Rule> inputEntryList = new LinkedHashMap<String, InputEntry_Rule>();
		ArrayList<String> patternStrList = new ArrayList<String>();
		patternStrList = DmnFreelingUtils.readPatternFile(DmnFilesUrl.INPUTENTRY_UNDERACTION_PATTERN.toString());
		for (String patternStr : patternStrList) {
			patternStr = patternStr.replace("[actionToken]", mainActionToken);
			TregexPattern pattern = TregexPattern.compile(patternStr);
			TregexMatcher matcher = pattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree tResultAction = matcher.getNode("resultAction");
				Input_Dmn input = null;
				if (tResultAction != null) {
					String resultActionToken = DmnFreelingUtils.getTokenFromNode(tResultAction.label().value());
					InputEntry_Rule inputEntry_Rule = getInputEntryFromAction(resultActionToken, input, tNegation);
					inputEntryList.put(inputEntry_Rule.getId(), inputEntry_Rule);
					inputEntryList.putAll(getInputEntryUnderAction(resultActionToken, tResultAction, tNegation));
				}
			}
		}
		return inputEntryList;
	}

	private Input_Dmn getInputFromInputEntryNull(String InputEntryToken) {
		Input_Dmn input = null;
		String name = tokens.get(InputEntryToken).getForm().toLowerCase();
		if (days.contains(name)) {
			name = "day";
			input = new Input_Dmn(InputEntryToken, name, name.replaceAll("[^A-Za-z0-9]", ""), TypeRef_Table.STRING);
		} else if (months.contains(name)) {
			name = "month";
			input = new Input_Dmn(InputEntryToken, name, name.replaceAll("[^A-Za-z0-9]", ""), TypeRef_Table.STRING);
		} else if (years.contains(name)) {
			name = "year";
			input = new Input_Dmn(InputEntryToken, name, name.replaceAll("[^A-Za-z0-9]", ""), TypeRef_Table.STRING);
		} else if (weather.contains(name)) {
			name = "weather";
			input = new Input_Dmn(InputEntryToken, name, name.replaceAll("[^A-Za-z0-9]", ""), TypeRef_Table.STRING);
		}
		return input;
	}

	public LinkedHashMap<String, InputEntry_Rule> getInputEntryFromNoun(String resultNounToken, Input_Dmn input,
			Tree tNegation) throws IOException {

		if (input == null) {
			input = getInputFromInputEntryNull(resultNounToken);
		}
		LinkedHashMap<String, InputEntry_Rule> inputEntryList = new LinkedHashMap<String, InputEntry_Rule>();
		String entryName = functions.getEntryName(resultNounToken);
		// List<Input_Dmn> inputs = new ArrayList<>(decisionTable.getInputs().values());
		Boolean negation = DmnFreelingUtils.isNegation(resultNounToken, trees);
		if (tNegation != null)
			negation = true;
		InputEntry_Rule inputEntry = new InputEntry_Rule(resultNounToken, entryName, input, negation);
		if (inputEntry != null) {
			inputEntryList.put(inputEntry.getId(), inputEntry);
		}
		return inputEntryList;
	}

}
