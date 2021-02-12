package edu.upc.freelingutils;

public enum Language {
	LANG("en");

	private String description;

	private Language(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
