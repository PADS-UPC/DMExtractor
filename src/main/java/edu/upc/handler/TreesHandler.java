package edu.upc.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.stanford.nlp.trees.Tree;
import edu.upc.entities.Activity;
import edu.upc.entities.Token;
import edu.upc.freelingutils.FilesUrl;
import edu.upc.freelingutils.FreelingUtils;

public class TreesHandler {
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;
	private final static String separator = FreelingUtils.separator;

	public TreesHandler(List<Tree> trees, LinkedHashMap<String, Token> tokens) {
		this.tokens = tokens;
		this.trees = trees;
	}

	public void addObjectsToTreeNodes(LinkedHashMap<String, Activity> activitiesList) throws IOException {
		for (Tree treeOfOneSentence : trees) {
			for (Entry<String, Activity> activity : activitiesList.entrySet()) {
				addRoleArgumentToTreeNode(activity.getValue(), treeOfOneSentence);
			}
		}
	}

	private void addRoleArgumentToTreeNode(Activity activity, Tree tree) {
		String node = tree.value();
		String newNode = makeNode(activity.getAction().getId(), tokens);
		if (node.equals(newNode)) {
			if (activity.getPatient() != null) {
				newNode = tree.value() + activity.getRole() + ":" + activity.getPatient().getId() + separator;
				tree.setValue(newNode);
			} else {
				newNode = tree.value() + activity.getRole() + ":null" + separator;
				tree.setValue(newNode);
			}
		}
		Iterator<?> treeIterator = tree.getChildrenAsList().iterator();
		while (treeIterator.hasNext()) {
			Tree tree2 = (Tree) treeIterator.next();
			addRoleArgumentToTreeNode(activity, tree2);
		}
	}

	public void removeObjectsToTreeNodes() throws IOException {
		for (Tree treeOfOneSentence : trees) {
			removeRoleArgumentToTreeNode(treeOfOneSentence);
		}
	}

	private void removeRoleArgumentToTreeNode(Tree tree) {
		String node = tree.value();
		String simpleNode = removeObject(node);
		tree.setValue(simpleNode);
		Iterator<?> treeIterator = tree.getChildrenAsList().iterator();
		while (treeIterator.hasNext()) {
			Tree tree2 = (Tree) treeIterator.next();
			removeRoleArgumentToTreeNode(tree2);
		}
	}

	public void refreshTree(LinkedHashMap<String, Activity> activitiesList) throws IOException {
		removeObjectsToTreeNodes();
		addObjectsToTreeNodes(activitiesList);
		Files.write(Paths.get(FilesUrl.TEMPORAL_TREE.toString()), trees.toString().getBytes());
	}

	public static String makeNode(Object object, LinkedHashMap<String, Token> tokens) {
		String word = separator + tokens.get(object).getId();
		word += separator + tokens.get(object).getPos();
		if (tokens.get(object).getLemma().equals("("))
			word += separator + "Fpa";
		else if (tokens.get(object).getLemma().equals(")"))
			word += separator + "Fpt";
		else
			word += separator + tokens.get(object).getLemma();
		word += separator + tokens.get(object).getCtag();
		word += separator;
		return word;
	}

	/*
	 * public static boolean isActivity(String node) { String node2 =
	 * removeObject(node); if (!node2.equals(node)) return true; return false; }
	 */
	public static String removeObject(String text) {
		String textTmp = text.replaceAll(":t[1-9][0-9]?.*|:null" + separator, "");
		if (!text.equals(textTmp)) {
			Integer pos = text.lastIndexOf(separator);
			textTmp = text.substring(0, pos);
			pos = textTmp.lastIndexOf(separator);
			text = text.substring(0, pos + 1);
		}
		return text;
	}

	public List<Tree> getTrees() {
		return trees;
	}

	public void setTrees(List<Tree> trees) {
		this.trees = trees;
	}
}
