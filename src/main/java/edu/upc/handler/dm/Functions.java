package edu.upc.handler.dm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.upc.entities.Activity;
import edu.upc.entities.Token;
import edu.upc.freelingutils.dm.DmnFilesUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;
import edu.upc.freelingutils.dm.TypeRef_Table;

public class Functions {
	private ArrayList<String> posTagForEntryName;
	private ArrayList<String> prepositionsForEntryName;
	private ArrayList<String> posTagForInputDataName;
	private ArrayList<String> prepositionsForInputDataName;

	private LinkedHashMap<String, Token> tokens;
	private String beginToken, endToken;
	private List<Tree> trees;
	private LinkedHashMap<String, Activity> activitiesList;
	private TypeRef_Table type;

	private ArrayList<String> namesToRemove = new ArrayList<String>(
			Arrays.asList("in", "on", "to", "with", "whenever", "otherwise", "when"));

	private ArrayList<String> greaterOperators = new ArrayList<String>();
	private ArrayList<String> lessOperators = new ArrayList<String>();

	public Functions(LinkedHashMap<String, Token> tokens, List<Tree> trees,
			LinkedHashMap<String, Activity> activitiesList) throws IOException {
		this.tokens = tokens;
		this.trees = trees;
		this.activitiesList = activitiesList;
		posTagForEntryName = new ArrayList<String>();
		posTagForEntryName = DmnFreelingUtils.readPatternFile(DmnFilesUrl.POSTAG_FOR_ENTRYNAME.toString());
		prepositionsForEntryName = new ArrayList<String>();
		prepositionsForEntryName = DmnFreelingUtils.readPatternFile(DmnFilesUrl.PREPOSITIONS_FOR_ENTRYNAME.toString());

		posTagForInputDataName = new ArrayList<String>();
		posTagForInputDataName = DmnFreelingUtils.readPatternFile(DmnFilesUrl.POSTAG_FOR_INPUTDATANAME.toString());
		prepositionsForInputDataName = new ArrayList<String>();
		prepositionsForInputDataName = DmnFreelingUtils
				.readPatternFile(DmnFilesUrl.PREPOSITIONS_FOR_INPUTDATANAME.toString());

		this.setType(TypeRef_Table.STRING);
		//
		greaterOperators.addAll(Arrays.asList("great", "more", "high", "above","exceed"));
		//
		lessOperators.addAll(Arrays.asList("less", "fewer"));
		//
		//Verbs: exceed, begin, start = >

	}

	private String getNewTokenAfterToRemoveName(String operatorToken) {
		ArrayList<String> patterns = new ArrayList<String>();
		if (namesToRemove.contains(tokens.get(operatorToken).getLemma())) {
			patterns.add("/¦t.*¦/=result > /¦" + operatorToken + "¦/");
			for (String patternStr : patterns) {
				TregexPattern pattern = TregexPattern.compile(patternStr);
				for (int i = 0; i < trees.size(); i++) {
					TregexMatcher matcher = pattern.matcher(trees.get(i));
					while (matcher.findNextMatchingNode()) {
						Tree tResult = matcher.getNode("result");
						if (tResult != null) {
							return DmnFreelingUtils.getTokenFromNode(tResult.label().value());
						}
					}
				}
			}
		}
		return null;
	}

	private String getTextInExpressionLanguage(String operatorToken) {
		String text = null;
		type = TypeRef_Table.STRING;
		ArrayList<String> patterns = new ArrayList<String>();
		patterns.clear();
		if (greaterOperators.contains(tokens.get(operatorToken).getLemma())) {
			patterns.add("/¦number¦/=number1 >> (/¦" + operatorToken + "¦/ !<< /¦and¦|¦or¦/)");
			String lessOperatorsCombinated = DmnFreelingUtils.joinStringsListWithOrSeparator(lessOperators);
			patterns.add("/¦number¦/=number1 < (/¦and¦/ << /¦" + lessOperatorsCombinated + "¦/ >> (/¦" + operatorToken
					+ "¦/ << (/¦number¦/=number2 !<< /¦and¦/)))");
			patterns.add("/¦number¦/=number1 > (/¦" + operatorToken + "¦/ < (/¦and¦/ << (/¦" + lessOperatorsCombinated
					+ "¦/ << (/¦number¦/=number2 !<< /¦and¦/))))");
			String[] numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 1) {
					text = ">" + numbers[0];
					type = TypeRef_Table.NUMERIC;
					return text;
				} else if (numbers.length == 2) {
					text = "[" + numbers[0] + ".." + numbers[1] + "]";
					type = TypeRef_Table.NUMERIC;
					return text;
				}
			}
			patterns.clear();
			patterns.add("/¦number¦/=number1 >> (/¦" + operatorToken + "¦/ << (/¦or¦/ << /¦equal¦/) )");
			numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 1) {
					text = ">=" + numbers[0];
					type = TypeRef_Table.NUMERIC;
					return text;
				}
			}

		} else if (lessOperators.contains(tokens.get(operatorToken).getLemma())) {
			// patterns.add("/¦number¦/=number1 >> (/¦" + operatorToken + "¦/ !<<
			// /¦and¦|¦or¦/)");
			patterns.add("/¦number¦/=number1 >> /¦" + operatorToken + "¦/ !<< /¦and¦|¦or¦/ !>> /¦and¦|¦or¦/");
			String[] numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 1) {
					text = "<" + numbers[0];
					type = TypeRef_Table.NUMERIC;
					return text;
				}
			}
			patterns.clear();
			patterns.add("/¦number¦/=number1 >> (/¦" + operatorToken + "¦/ << (/¦or¦/ << /¦equal¦/) )");
			numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 1) {
					text = "<=" + numbers[0];
					type = TypeRef_Table.NUMERIC;
					return text;
				}
			}
		} else if (tokens.get(operatorToken).getLemma().equals("between")) {
			patterns.add("/¦number¦/=number1 >> /¦" + operatorToken + "¦/ < (/¦and¦/ < /¦number¦/=number2)");
			patterns.add("/¦number¦/=number1 < /¦" + operatorToken + "¦/ < (/¦and¦/ < /¦number¦/=number2)");
			String[] numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 2) {
					text = "[" + numbers[0] + ".." + numbers[1] + "]";
					type = TypeRef_Table.NUMERIC;
					return text;
				}
			}
		} else if (tokens.get(operatorToken).getLemma().equals("equal")) {
			patterns.add("/¦number¦/=number1 > (/¦to¦/ > /¦" + operatorToken + "¦/)");
			String[] numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 1) {
					text = numbers[0];
					type = TypeRef_Table.NUMERIC;
					return text;
				}
			}
		} else if (tokens.get(operatorToken).getPos().equals("number")) {
			type = TypeRef_Table.NUMERIC;
			String stringWithOrBarFromList = DmnFreelingUtils.joinStringsListWithOrSeparator(greaterOperators);
			patterns.add("/¦" + operatorToken + "¦/=number1 << (/¦or¦/ << /¦" + stringWithOrBarFromList + "¦/)");
			patterns.add("/¦" + operatorToken + "¦/=number1 < /¦more¦/ < /¦than¦/");
			String[] numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 1) {
					text = ">" + numbers[0];
					return text;
				}
			}
			stringWithOrBarFromList = DmnFreelingUtils.joinStringsListWithOrSeparator(lessOperators);
			patterns.clear();
			patterns.add("/¦" + operatorToken + "¦/=number1 << (/¦or¦/ << /¦" + stringWithOrBarFromList + "¦/)");
			numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 1) {
					text = "<" + numbers[0];
					return text;
				}
			}
			text = tokens.get(operatorToken).getLemma();
		} else if (tokens.get(operatorToken).getLemma().equals(">")) {
			patterns.add("/¦number¦/=number1 > (/¦" + operatorToken
					+ "¦/ < (/¦and¦/ < (/¦punctuation¦/ < /¦number¦/=number2)))");
			String[] numbers = getTextFromTwoNumbers(patterns);
			if (numbers != null) {
				if (numbers.length == 2) {
					text = "[" + numbers[0] + ".." + numbers[1] + "]";
					text = text.replace("=", "");
					type = TypeRef_Table.NUMERIC;
					return text;
				}
			}
		}

		return text;
	}

	private String[] getTextFromTwoNumbers(ArrayList<String> patterns) {
		for (String patternStr : patterns) {
			TregexPattern pattern = TregexPattern.compile(patternStr);
			for (int i = 0; i < trees.size(); i++) {
				TregexMatcher matcher = pattern.matcher(trees.get(i));
				while (matcher.findNextMatchingNode()) {
					Tree tNumber1 = matcher.getNode("number1");
					Tree tNumber2 = matcher.getNode("number2");
					String number1Token = null;
					String number2Token = null;
					if (tNumber1 != null)
						number1Token = DmnFreelingUtils.getTokenFromNode(tNumber1.label().value());
					if (tNumber2 != null)
						number2Token = DmnFreelingUtils.getTokenFromNode(tNumber2.label().value());
					if (tNumber1 != null && tNumber2 == null) {
						String[] numbers = { tokens.get(number1Token).getLemma() };
						return numbers;
					}
					if (tNumber1 != null && tNumber2 != null) {
						String[] numbers = { tokens.get(number1Token).getLemma(), tokens.get(number2Token).getLemma() };
						return numbers;
					}
				}
			}
		}
		return null;
	}

	private Tree getTreeNode(String patientToken) {
		TregexPattern pattern = TregexPattern.compile("/¦" + patientToken + "¦/=result ");
		for (int i = 0; i < trees.size(); i++) {
			TregexMatcher matcher = pattern.matcher(trees.get(i));
			while (matcher.findNextMatchingNode()) {
				Tree tResult = matcher.getNode("result");
				if (tResult != null)
					return tResult;
			}
		}
		return null;
	}

	public String getEntryName(String patientToken) {
		String newToken = getNewTokenAfterToRemoveName(patientToken);
		if (newToken != null)
			patientToken = newToken;

		String text = getTextInExpressionLanguage(patientToken);
		if (text != null)
			return text;
		else
			text = "";

		beginToken = null;
		endToken = null;
		if (tokens.containsKey(patientToken)) {
			if (posTagForEntryName.contains(tokens.get(patientToken).getPos())
					|| prepositionsForEntryName.contains(tokens.get(patientToken).getLemma())) {
				beginToken = patientToken;
				endToken = patientToken;
				Tree tree = getTreeNode(patientToken);
				if (activitiesList.containsKey(patientToken))
					return null;
				findSubjectInSubtreesAndMakeName(patientToken, tree);
			}
			if (beginToken != null) {
				String sentenceNumber = beginToken.replace(".", " ").split(" ", 2)[0];
				Integer initNumber = Integer.parseInt(beginToken.replace(".", " ").split(" ", 2)[1]);
				Integer endNumber = Integer.parseInt(endToken.replace(".", " ").split(" ", 2)[1]);
				for (int i = initNumber; i <= endNumber; i++) {
					text += tokens.get(sentenceNumber + "." + i).getForm() + " ";
				}
			}
			if (!text.trim().isEmpty()) {
				return text.trim();
			}
		}
		return null;
	}

	private void findSubjectInSubtreesAndMakeName(String patientToken, Tree tree) {
		if (activitiesList.containsKey(patientToken))
			return;
		if (posTagForEntryName.contains(tokens.get(patientToken).getPos())
				|| prepositionsForEntryName.contains(tokens.get(patientToken).getLemma())) {
			ArrayList<String> list = new ArrayList<String>();
			posTagForEntryName.forEach(action -> list.add(action));
			prepositionsForEntryName.forEach(action -> list.add(action));
			String stringWithOrBarFromList = DmnFreelingUtils.joinStringsListWithOrSeparator(list);
			// TregexPattern pattern = TregexPattern.compile(
			// "/¦" + stringWithOrBarFromList + "¦/=result > /¦" + patientToken + "¦/ !$,
			// /¦,¦/ !</¦'s¦/");
			TregexPattern pattern = TregexPattern
					.compile("/¦" + stringWithOrBarFromList + "¦/=result > /¦" + patientToken + "¦/ !$, /¦,¦/");
			TregexMatcher matcher = pattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree tNounResult = matcher.getNode("result");
				String patientToken2 = DmnFreelingUtils.getTokenFromNode(tNounResult.label().value());
				if (DmnFreelingUtils.isTokenALessThanTokenB(endToken, patientToken2))
					endToken = patientToken2;
				else if (DmnFreelingUtils.isTokenALessThanTokenB(patientToken2, beginToken)) {
					beginToken = patientToken2;
				}
				findSubjectInSubtreesAndMakeName(patientToken2, tNounResult);
			}
		}
	}

	public String getInputDataName(String patientToken) {
		String newToken = getNewTokenAfterToRemoveName(patientToken);
		if (newToken != null)
			patientToken = newToken;
		String text = "";
		beginToken = null;
		endToken = null;
		if (tokens.containsKey(patientToken)) {
			if (posTagForEntryName.contains(tokens.get(patientToken).getPos())
					|| prepositionsForEntryName.contains(tokens.get(patientToken).getLemma())) {
				beginToken = patientToken;
				endToken = patientToken;
				Tree tree = getTreeNode(patientToken);
				if (activitiesList.containsKey(patientToken))
					return null;
				findSubjectInSubtreesAndMakeInputDataName(patientToken, tree);
			}
			if (beginToken != null) {
				String sentenceNumber = beginToken.replace(".", " ").split(" ", 2)[0];
				Integer initNumber = Integer.parseInt(beginToken.replace(".", " ").split(" ", 2)[1]);
				Integer endNumber = Integer.parseInt(endToken.replace(".", " ").split(" ", 2)[1]);
				for (int i = initNumber; i <= endNumber; i++) {
					text += tokens.get(sentenceNumber + "." + i).getForm() + " ";
				}
			}
			if (!text.trim().isEmpty()) {
				return text.trim();
			}
		}
		return null;
	}

	private void findSubjectInSubtreesAndMakeInputDataName(String patientToken, Tree tree) {
		if (activitiesList.containsKey(patientToken))
			return;
		if (posTagForInputDataName.contains(tokens.get(patientToken).getPos())
				|| prepositionsForInputDataName.contains(tokens.get(patientToken).getLemma())) {
			ArrayList<String> list = new ArrayList<String>();
			posTagForInputDataName.forEach(action -> list.add(action));
			prepositionsForInputDataName.forEach(action -> list.add(action));
			String stringWithOrBarFromList = DmnFreelingUtils.joinStringsListWithOrSeparator(list);
			TregexPattern pattern = TregexPattern
					.compile("/¦" + stringWithOrBarFromList + "¦/=result > /¦" + patientToken + "¦/ !$, /¦,¦/");
			TregexMatcher matcher = pattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree tNounResult = matcher.getNode("result");
				String patientToken2 = DmnFreelingUtils.getTokenFromNode(tNounResult.label().value());
				if (DmnFreelingUtils.isTokenALessThanTokenB(endToken, patientToken2))
					endToken = patientToken2;
				else if (DmnFreelingUtils.isTokenALessThanTokenB(patientToken2, beginToken)) {
					beginToken = patientToken2;
				}
				findSubjectInSubtreesAndMakeName(patientToken2, tNounResult);
			}
		}
	}

	public TypeRef_Table getType() {
		return type;
	}

	public void setType(TypeRef_Table type) {
		this.type = type;
	}
}
