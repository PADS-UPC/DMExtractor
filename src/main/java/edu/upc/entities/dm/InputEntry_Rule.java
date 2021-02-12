package edu.upc.entities.dm;

public class InputEntry_Rule {

	private String id;
	private String text;
	private Input_Dmn input;
	private Boolean negation;

	public InputEntry_Rule(String id, String text, Input_Dmn input, Boolean negation) {
		super();
		this.id = id;
		this.text = text;
		this.input = input;
		this.negation = negation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Input_Dmn getInput() {
		return input;
	}

	public void setInput(Input_Dmn input) {
		this.input = input;
	}

	public Boolean getNegation() {
		return negation;
	}

	public void setNegation(Boolean negation) {
		this.negation = negation;
	}

	@Override
	public String toString() {
		return "InputEntry_Rule [id=" + id + ", text=" + text + ", input=" + input + ", negation=" + negation + "]";
	}

}
