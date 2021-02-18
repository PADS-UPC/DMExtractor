package edu.upc.handler.dm;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.stanford.nlp.trees.Tree;
import edu.upc.entities.Activity;
import edu.upc.entities.Predicate;
import edu.upc.entities.PredicateArgument;
import edu.upc.entities.Token;
import edu.upc.entities.dm.DecisionTable_Dmn;
import edu.upc.entities.dm.OutputEntry_Rule;
import edu.upc.freelingutils.dm.DmnFreelingUtils;
import edu.upc.freelingutils.dm.TypeRef_Table;

public class OutputEntryHandler {
	private LinkedHashMap<String, Activity> activitiesList;
	private LinkedHashMap<String, Predicate> predicates;
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;
	private Functions functions;

	public OutputEntryHandler(LinkedHashMap<String, Activity> activitiesList,
			LinkedHashMap<String, Predicate> predicates, LinkedHashMap<String, Token> tokens, List<Tree> trees)
			throws IOException {
		this.predicates = predicates;
		this.activitiesList = activitiesList;
		this.trees = trees;
		this.tokens = tokens;

		functions = new Functions(tokens, trees, activitiesList);
	}

	public OutputEntry_Rule getOutputEntryRole(DecisionTable_Dmn decisionTable, String decisionActionToken,
			String decisionNounToken) {
		OutputEntry_Rule outputEntry = null;
		if (decisionActionToken != null) {
			if (activitiesList.get(decisionActionToken).getPatient() != null) {
				String id = activitiesList.get(decisionActionToken).getPatient().getId();
				Predicate predicate = predicates.get(decisionActionToken);
				Boolean a2Found = false;
				for (Entry<String, PredicateArgument> argument : predicate.getA1PlusArguments().entrySet()) {
					if (DmnFreelingUtils.argumentDifferentToA1(predicate, argument, tokens)) {
						Boolean negation = DmnFreelingUtils.isNegation(id, trees);
						String name = functions.getEntryName(argument.getKey());
						if (name != null) {
							outputEntry = new OutputEntry_Rule(id, name, negation);
							a2Found = true;
						}
						break;
					}
				}
				if (!a2Found && predicate.getA1PlusArguments().get(id).getRole().equals("A1")) {
					PredicateArgument arg = DmnFreelingUtils.getSecondA1(predicate, id, tokens);
					if (arg != null) {
						Boolean negation = DmnFreelingUtils.isNegation(arg.getHead_token(), trees);
						String name = functions.getEntryName(arg.getHead_token());
						if (name != null)
							outputEntry = new OutputEntry_Rule(arg.getHead_token(), name, negation);
					} else {
						Boolean negation = DmnFreelingUtils.isNegation(id, trees);
						String name = functions.getEntryName(id);
						if (name != null)
							outputEntry = new OutputEntry_Rule(id, name, negation);
					}
				}
			}
		} else if (decisionNounToken != null) {
			Boolean negation = DmnFreelingUtils.isNegation(decisionNounToken, trees);
			outputEntry = new OutputEntry_Rule(decisionNounToken, functions.getEntryName(decisionNounToken), negation);
		} else
			return null;

		if (outputEntry == null && decisionActionToken != null) {
			String text = tokens.get(decisionActionToken).getLemma();
			Boolean negation = DmnFreelingUtils.isNegation(decisionActionToken, trees);
			outputEntry = new OutputEntry_Rule(decisionActionToken, text, negation);
		}
		if (decisionTable.getName().toLowerCase().trim().contains(outputEntry.getText().toLowerCase().trim())) {
			outputEntry.setText("true");
			decisionTable.getOutput().setTypeRef(TypeRef_Table.BOOLEAN);
		}
		return outputEntry;
	}
}
