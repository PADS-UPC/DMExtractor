package edu.upc.handler.dm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.stanford.nlp.trees.Tree;
import edu.upc.entities.Token;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.freelingutils.dm.DmnFilesUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;
import edu.upc.entities.Activity;

public class DmTreesHandler {
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;
	private final static String separator = DmnFreelingUtils.separator;

	public DmTreesHandler(List<Tree> trees, LinkedHashMap<String, Token> tokens) {
		this.tokens = tokens;
		this.trees = trees;
	}

	public void refreshTree(LinkedHashMap<String, Activity> activitiesList) throws IOException {
		removeObjectsToTreeNodes();
		addObjectsToTreeNodes(activitiesList);
		Files.write(Paths.get(DmnFilesUrl.TEMPORAL_TREE.toString()), trees.toString().getBytes());
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

	public void refreshTreeWithDecisions(LinkedHashMap<String, Decision_Dmn> decisions) throws IOException {
		for (Tree treeOfOneSentence : trees) {
			for (Entry<String, Decision_Dmn> decision : decisions.entrySet()) {
				addRoleArgumentToTreeNode(decision.getValue(), treeOfOneSentence);
			}
		}
		Files.write(Paths.get(DmnFilesUrl.TEMPORAL_TREE.toString()), trees.toString().getBytes());
	}

	private void addRoleArgumentToTreeNode(Decision_Dmn decision, Tree tree) {
		String node = tree.value();
		String newNode = makeNode(decision.getDrgElement().getToken(), tokens);
		if (node.equals(newNode)) {
			newNode = tree.value() + decision.getDrgElement().getType() + ":" + decision.getAction().getId()
					+ separator;
			tree.setValue(newNode);
		}
		Iterator<?> treeIterator = tree.getChildrenAsList().iterator();
		while (treeIterator.hasNext()) {
			Tree tree2 = (Tree) treeIterator.next();
			addRoleArgumentToTreeNode(decision, tree2);
		}
	}

	public void refreshTreeWithInputs(LinkedHashMap<String, InputData_Dmn> inputs) throws IOException {
		for (Tree treeOfOneSentence : trees) {
			for (Entry<String, InputData_Dmn> input : inputs.entrySet()) {
				addRoleArgumentToTreeNode(input.getValue(), treeOfOneSentence);
			}
		}
		Files.write(Paths.get(DmnFilesUrl.TEMPORAL_TREE.toString()), trees.toString().getBytes());
	}

	private void addRoleArgumentToTreeNode(InputData_Dmn inputs, Tree tree) {
		String node = tree.value();
		String newNode = makeNode(inputs.getDrgElement().getToken(), tokens);
		if (node.equals(newNode)) {
			newNode = tree.value() + inputs.getDrgElement().getType() + ":"
					+ inputs.getDecision().getDrgElement().getToken() + separator;
			tree.setValue(newNode);
		}
		Iterator<?> treeIterator = tree.getChildrenAsList().iterator();
		while (treeIterator.hasNext()) {
			Tree tree2 = (Tree) treeIterator.next();
			addRoleArgumentToTreeNode(inputs, tree2);
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

	public static String makeNode(Object object, LinkedHashMap<String, Token> tokens) {
		String word = null;
		if (tokens.containsKey(object)) {
			word = separator + tokens.get(object).getId();
			word += separator + tokens.get(object).getPos();
			if (tokens.get(object).getLemma().equals("("))
				word += separator + "Fpa";
			else if (tokens.get(object).getLemma().equals(")"))
				word += separator + "Fpt";
			else
				word += separator + tokens.get(object).getLemma();
			word += separator + tokens.get(object).getCtag();
			word += separator;
		}
		return word;
	}

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
