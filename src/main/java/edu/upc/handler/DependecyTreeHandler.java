package edu.upc.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.Tree;
import edu.upc.entities.Token;
import edu.upc.handler.dm.DmTreesHandler;

public class DependecyTreeHandler {
	private LinkedHashMap<String, Token> tokens;
	private List<Tree> trees;

	public DependecyTreeHandler(LinkedHashMap<String, Token> tokens) {
		super();
		this.tokens = tokens;
		this.trees = new ArrayList<Tree>();
	}

	public List<Tree> parseDependencies(JSONObject jsonObject) {
		JSONArray dependenciesArray = (JSONArray) jsonObject.get("dependencies");
		if (dependenciesArray != null) {
			Iterator<?> dependenciesIterator = dependenciesArray.iterator();
			while (dependenciesIterator.hasNext()) {
				jsonObject = (JSONObject) dependenciesIterator.next();
				tokens.get(jsonObject.get("token")).setFunction(jsonObject.get("function").toString());
				String word = DmTreesHandler.makeNode(jsonObject.get("token"), tokens);
				Tree tree = new LabeledScoredTreeNode(new Word(word));
				trees.add(tree);
				parseDependenciesChildren(jsonObject, jsonObject.get("token").toString(), tree);
			}
			return trees;
		} else
			System.out.println("dependencies Not Found");
		return null;
	}

	private void parseDependenciesChildren(JSONObject jsonObject, String parentToken, Tree tree) {

		JSONArray childrenArray = (JSONArray) jsonObject.get("children");
		if (childrenArray != null) // Exist children
		{
			Iterator<?> childrenIterator = childrenArray.iterator();
			while (childrenIterator.hasNext()) {
				jsonObject = (JSONObject) childrenIterator.next();
				Object token = jsonObject.get("token");

				tokens.get(token).setFunction(jsonObject.get("function").toString());
				tokens.get(token).setParent(parentToken);
				tokens.get(parentToken).getChildren().add(token.toString());

				String word = DmTreesHandler.makeNode(token, tokens);
				Tree tChild = new LabeledScoredTreeNode(new Word(word));
				tree.addChild(tChild);
				parseDependenciesChildren(jsonObject, token.toString(), tChild);
			}
		}
	}

}
