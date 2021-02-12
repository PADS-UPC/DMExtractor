package edu.upc.freelingutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.upc.entities.Activity;

public class FreelingUtils {
	public static final String separator = "¦";

	public static boolean isNounAction(String wn, String lemma) throws IOException {
		return isNounActionToAdd(wn, lemma, false);
	}

	public static boolean isNounActionToAdd(String wn, String lemma, Boolean toAdd) throws IOException {
		ArrayList<String[]> nounActivityList = null;
		if (toAdd)
			nounActivityList = readNounActivityFile(FilesUrl.FREEELING_NOUN_TO_ADD_CONFIG_FILE.toString());
		else
			nounActivityList = readNounActivityFile(FilesUrl.FREEELING_NOUN_CONFIG_FILE.toString());
		if (wn != null && lemma != null)
			for (int i = 0; i < nounActivityList.size(); i++) {
				if (wn.equals(nounActivityList.get(i)[0]) && nounActivityList.get(i)[1].contains(lemma)) {
					return true;
				}
			}
		return false;
	}

	public static String getTokenFromNode(String node) {
		String word = node.replace(separator, " ");
		String[] words = word.split("\\s");
		return words[1];
	}

	public static String joinStringsListWithOrSeparator(ArrayList<String> textsArrayList) {
		String str = "";
		for (int i = 0; i < textsArrayList.size(); i++) {
			str += textsArrayList.get(i);
			if (i != textsArrayList.size() - 1)
				str += "|";
		}
		return str;
	}

	public static String readFile(String fileName) throws IOException {
		String text = "";
		text = new String(Files.readAllBytes(Paths.get(fileName)));
		return text;
	}

	public static ArrayList<String> readPatternFile(String fileName) throws IOException {
		File file = new File(fileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		ArrayList<String> list = new ArrayList<String>();
		while ((line = bufferedReader.readLine()) != null) {
			if (!line.startsWith("#") && !line.isEmpty())
				list.add(line);
		}
		bufferedReader.close();
		fileReader.close();
		return list;
	}

	public static ArrayList<String[]> readJudgeAnnotationFile(String fileName) throws IOException {
		File file = new File(fileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		ArrayList<String[]> list = new ArrayList<String[]>();
		while ((line = bufferedReader.readLine()) != null) {
			if (!line.isEmpty()) {
				String[] columns = line.split("\\t");
				String[] columns2 = columns[1].split("\\s");
				if (columns2[0].equals("Condition") || columns2[0].equals("Action")) {
					String id = columns[0];
					String actionCondition = columns2[0];
					String begin = columns2[1];
					String end = columns2[2];
					String text = columns[2];
					String words[] = { id, actionCondition, begin, end, text };
					list.add(words);
				} else if (columns2[0].equals("Conflict") || columns2[0].equals("Sequence")) {
					String id = columns[0];
					String actionCondition = columns2[0];
					String begin = columns2[1];
					String end = columns2[2];
					String words[] = { id, actionCondition, begin, end };
					list.add(words);
				}
			}
		}
		bufferedReader.close();
		fileReader.close();
		return list;
	}

	public static ArrayList<String[]> readNounActivityFile(String fileName) throws IOException {
		File file = new File(fileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		ArrayList<String[]> list = new ArrayList<String[]>();
		while ((line = bufferedReader.readLine()) != null) {
			if (!line.contains("#") && !line.isEmpty()) {
				String[] columns = line.split("\\s");
				String id = columns[0];
				String text = line.substring(id.length() + 1);
				String words[] = { id, text };
				list.add(words);
			}
		}
		bufferedReader.close();
		fileReader.close();
		return list;
	}

	public static String getFileNameWithoutExtension(File file) {
		String fileName = "";
		try {
			if (file != null && file.exists()) {
				String name = file.getName();
				fileName = name.replaceFirst("[.][^.]+$", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fileName = "";
		}
		return fileName;

	}

	public static ArrayList<Activity> fillActivitiesFromTree(Tree tree,
			LinkedHashMap<String, Activity> activitiesList) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		ArrayList<String> patternStrList = new ArrayList<String>();
		patternStrList.add("/" + FreelingUtils.separator + "*:.*" + FreelingUtils.separator + "/=result");
		String resultToken = "";
		for (String patternStr : patternStrList) {
			TregexPattern pattern = TregexPattern.compile(patternStr);
			TregexMatcher matcher = pattern.matcher(tree);
			while (matcher.findNextMatchingNode()) {
				Tree tResult = matcher.getNode("result");
				if (tResult != null) {
					resultToken = FreelingUtils.getTokenFromNode(tResult.label().value());
					if (activitiesList.containsKey(resultToken))
						activities.add(activitiesList.get(resultToken));
				}
			}
		}
		return activities;
	}

	public static Activity findLastActivity(Tree tree, LinkedHashMap<String, Activity> activitiesList) {
		ArrayList<Activity> activities = fillActivitiesFromTree(tree, activitiesList);
		activities = sortActivities(activities);
		if (activities.size() > 0)
			return activities.get(activities.size() - 1);
		return null;
	}

	public static Activity findFirstActivity(Tree tree, LinkedHashMap<String, Activity> activitiesList) {
		ArrayList<Activity> activities = fillActivitiesFromTree(tree, activitiesList);
		activities = sortActivities(activities);
		if (activities.size() > 0)
			return activities.get(0);
		return null;
	}

	public static Activity findFirstCondition(Tree tree, LinkedHashMap<String, Activity> activitiesList) {
		ArrayList<Activity> activities = fillActivitiesFromTree(tree, activitiesList);
		activities = sortActivities(activities);
		for (int i = 0; i < activities.size(); i++) {
			if (activities.get(i).getRole() == ActionType.CONDITION)
				return activities.get(i);
		}
		return null;
	}

	public static ArrayList<Activity> sortActivities(ArrayList<Activity> activities) {
		ArrayList<Integer> array = new ArrayList<Integer>();
		for (int i = 0; i < activities.size(); i++) {
			String number = activities.get(i).getId().replace(".", " ").split(" ", 2)[1];
			array.add(Integer.parseInt(number));
		}
		Collections.sort(array);
		ArrayList<Activity> activitiesTmp = new ArrayList<Activity>();
		for (int i = 0; i < array.size(); i++) {
			String token = activities.get(i).getId().replace(".", " ").split(" ", 2)[0] + "." + array.get(i);
			for (int j = 0; j < activities.size(); j++) {
				if (token.equals(activities.get(j).getId()))
					activitiesTmp.add(activities.get(j));
			}
		}
		return activitiesTmp;
	}

	public static Tree makeSuperTree(List<Tree> trees) throws IOException {
		Tree treeDocument = new LabeledScoredTreeNode(
				new Word(FreelingUtils.separator + "DOCUMENT" + FreelingUtils.separator));
		Tree treeParagraph = new LabeledScoredTreeNode(
				new Word(FreelingUtils.separator + "PARAGRAPH-1" + FreelingUtils.separator));
		treeDocument.addChild(treeParagraph);

		for (int i = 0; i < trees.size(); i++) {
			treeParagraph.addChild(trees.get(i));
		}
		return treeDocument;
	}

	public static Boolean isTokenALessThanTokenB(String tokenA, String tokenB) {
		String sentenceNumberA = tokenA.replace(".", " ").split(" ", 2)[0];
		String objectNumberA = tokenA.replace(".", " ").split(" ", 2)[1];
		String sentenceNumberB = tokenB.replace(".", " ").split(" ", 2)[0];
		String objectNumberB = tokenB.replace(".", " ").split(" ", 2)[1];

		if (sentenceNumberA.equals(sentenceNumberB)) {
			if (Integer.parseInt(objectNumberA) < Integer.parseInt(objectNumberB))
				return true;
		}
		return false;
	}
}
