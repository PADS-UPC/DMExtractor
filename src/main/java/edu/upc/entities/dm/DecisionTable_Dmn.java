package edu.upc.entities.dm;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DecisionTable_Dmn {
	private String id;
	private String name;
	private LinkedHashMap<String, Input_Dmn> inputs;
	private Output_Dmn output;
	private ArrayList<Rule_Table> rules;
	private Decision_Dmn decision;

	public DecisionTable_Dmn(String id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.inputs = null;
		this.output = null;
		this.rules = null;
		this.decision = null;
	}

	public DecisionTable_Dmn(String id, String name, LinkedHashMap<String, Input_Dmn> inputs, Output_Dmn output,
			ArrayList<Rule_Table> rules, Decision_Dmn decision) {
		super();
		this.id = id;
		this.name = name;
		this.inputs = inputs;
		this.output = output;
		this.rules = rules;
		this.decision = null;
		this.decision = decision;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedHashMap<String, Input_Dmn> getInputs() {
		return inputs;
	}

	public void setInputs(LinkedHashMap<String, Input_Dmn> inputs) {
		this.inputs = inputs;
	}

	public Output_Dmn getOutput() {
		return output;
	}

	public void setOutput(Output_Dmn output) {
		this.output = output;
	}

	public ArrayList<Rule_Table> getRules() {
		return rules;
	}

	public void setRules(ArrayList<Rule_Table> rules) {
		this.rules = rules;
	}

	public Decision_Dmn getDecision() {
		return decision;
	}

	public void setDecision(Decision_Dmn decision) {
		this.decision = decision;
	}

	@Override
	public String toString() {
		return "DecisionTable_Dmn [id=" + id + ", name=" + name + ", inputs=" + inputs + ", output=" + output
				+ ", rules=" + rules + "]";
	}

}
