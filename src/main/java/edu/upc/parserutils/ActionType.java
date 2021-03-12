package edu.upc.parserutils;

public enum ActionType {
	EVENT("EVENT"), 
	CONDITION("CONDITION"),
	ACTION("ACTION"), 
	ACTIVITY("ACTIVITY"),
	DECISION("DECISION"),
	INPUTDATA("INPUTDATA"),
	INPUT("INPUT"),
	OUTPUT("OUTPUT"),
	INPUTENTRY("INPUTENTRY"),
	OUTTENTRY("OUTPUTENTRY");

	private final String description;

	ActionType(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

}
