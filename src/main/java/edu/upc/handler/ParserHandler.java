package edu.upc.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.json.simple.parser.ParseException;

import edu.stanford.nlp.trees.Tree;
import edu.upc.FreelingConnection;
import edu.upc.entities.Activity;
import edu.upc.entities.Agent;
import edu.upc.entities.Entity;
import edu.upc.entities.Patient;
import edu.upc.entities.Predicate;
import edu.upc.entities.Token;

public class ParserHandler {

	private LinkedHashMap<String, Token> tokens;
	private LinkedHashMap<String, Predicate> predicates;
	private LinkedHashMap<String, Entity> entities;
	private List<Tree> trees;
	private LinkedHashMap<String, Agent> agentsList;
	private LinkedHashMap<String, Patient> patientsList;
	private LinkedHashMap<String, Activity> activitiesList;
	private String freelingJsonString;

	public ParserHandler(String text) throws ParseException, IOException {
		FreelingConnection freelingConection = new FreelingConnection();
		String freelingJsonString = freelingConection.getJsonString(text);

		this.tokens = new LinkedHashMap<String, Token>();
		this.predicates = new LinkedHashMap<String, Predicate>();
		this.entities = new LinkedHashMap<String, Entity>();
		this.trees = new ArrayList<Tree>();
		this.agentsList = new LinkedHashMap<String, Agent>();
		this.patientsList = new LinkedHashMap<String, Patient>();
		this.activitiesList = new LinkedHashMap<String, Activity>();
		this.freelingJsonString = freelingJsonString;

		ParagraphHandler paragraphHandler = new ParagraphHandler(freelingJsonString);
		paragraphHandler.parceparagraphs();

		this.tokens = paragraphHandler.getTokens();
		this.predicates = paragraphHandler.getPredicates();
		this.entities = paragraphHandler.getEntities();
		this.trees = paragraphHandler.getTrees();

		AgentHandler agentHandler = new AgentHandler(trees, tokens, predicates, entities);
		agentsList = agentHandler.getAgentsList();

		PatientHandler patientsHandler = new PatientHandler(trees, tokens, predicates);
		patientsList = patientsHandler.getPatientsList();

		ActionHandler actionHandler = new ActionHandler(predicates, tokens, agentsList, patientsList);
		activitiesList = actionHandler.getActivitiesList();

	}

	public LinkedHashMap<String, Token> getTokens() {
		return tokens;
	}

	public void setTokens(LinkedHashMap<String, Token> tokens) {
		this.tokens = tokens;
	}

	public LinkedHashMap<String, Agent> getAgentsList() {
		return agentsList;
	}

	public void setAgentsList(LinkedHashMap<String, Agent> agentsList) {
		this.agentsList = agentsList;
	}

	public LinkedHashMap<String, Activity> getActivitiesList() {
		return activitiesList;
	}

	public void setActivitiesList(LinkedHashMap<String, Activity> activitiesList) {
		this.activitiesList = activitiesList;
	}

	public LinkedHashMap<String, Entity> getEntities() {
		return entities;
	}

	public void setEntities(LinkedHashMap<String, Entity> entities) {
		this.entities = entities;
	}

	public List<Tree> getTrees() {
		return trees;
	}

	public void setTrees(List<Tree> trees) {
		this.trees = trees;
	}

	public LinkedHashMap<String, Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(LinkedHashMap<String, Predicate> predicates) {
		this.predicates = predicates;
	}

	public LinkedHashMap<String, Patient> getPatientsList() {
		return patientsList;
	}

	public void setPatientsList(LinkedHashMap<String, Patient> patientsList) {
		this.patientsList = patientsList;
	}

	public String getFreelingJsonString() {
		return freelingJsonString;
	}

	public void setFreelingJsonString(String freelingJsonString) {
		this.freelingJsonString = freelingJsonString;
	}

}
