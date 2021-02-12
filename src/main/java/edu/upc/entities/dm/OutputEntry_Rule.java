package edu.upc.entities.dm;

public class OutputEntry_Rule {
	private String id;
	private String text;
	private Boolean negation;

	public OutputEntry_Rule(String id, String text, Boolean negation) {
		super();
		this.id = id;
		this.text = text;
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

	public Boolean getNegation() {
		return negation;
	}

	public void setNegation(Boolean negation) {
		this.negation = negation;
	}

	@Override
	public String toString() {
		return "OutputEntry_Rule [id=" + id + ", text=" + text + ", negation=" + negation + "]";
	}

}
