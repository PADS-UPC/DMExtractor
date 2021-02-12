package edu.upc.entities.dm;

import java.util.LinkedHashMap;

public class Rule_Table {

	private LinkedHashMap<String, InputEntry_Rule> inputEntries;
	private OutputEntry_Rule outputEntry;

	public Rule_Table(LinkedHashMap<String, InputEntry_Rule> inputEntries, OutputEntry_Rule outputEntry) {
		super();
		this.inputEntries = inputEntries;
		this.outputEntry = outputEntry;
	}

	public LinkedHashMap<String, InputEntry_Rule> getInputEntries() {
		return inputEntries;
	}

	public void setInputEntries(LinkedHashMap<String, InputEntry_Rule> inputEntries) {
		this.inputEntries = inputEntries;
	}

	public OutputEntry_Rule getOutputEntry() {
		return outputEntry;
	}

	public void setOutputEntry(OutputEntry_Rule outputEntry) {
		this.outputEntry = outputEntry;
	}

	@Override
	public String toString() {
		return "Rule_Table [inputEntries=" + inputEntries + ", outputEntry=" + outputEntry + "]";
	}

}
