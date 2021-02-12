package edu.upc.entities;

import edu.upc.freelingutils.ActionType;

public class Activity {

	private String id;
	private Agent agent;
	private Patient patient;
	private Action action;
	private ActionType role;
	private String pattern;
	private String text;
	private Integer begin;
	private Integer end;

	public Activity(String id, Action action) {
		super();
		this.id = id;
		this.role = null;
		this.agent = null;
		this.patient = null;
		this.action = action;
		this.pattern = null;
		this.text = "";
	}

	public Activity(String id, Agent agent, Action action, Patient patient) {
		super();
		this.id = id;
		this.role = ActionType.ACTION;
		this.agent = agent;
		this.patient = patient;
		this.action = action;
		this.pattern = null;
		this.text = "";
	}

	@Override
	public String toString() {
		return "Activity [id=" + id + ", " + agent + ", " + action + ", " + patient + ", pattern=" + pattern + ", Role="
				+ role + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ActionType getRole() {
		return role;
	}

	public void setRole(ActionType role) {
		this.role = role;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getBegin() {
		return begin;
	}

	public void setBegin(Integer begin) {
		this.begin = begin;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

}
