package edu.upc.entities.dm;

public class Requirement {
	Decision_Dmn decision;
	InputData_Dmn inputData;

	public Requirement(InputData_Dmn inputData, Decision_Dmn decision) {
		super();
		this.inputData = inputData;
		this.decision = decision;
	}

	public Decision_Dmn getDecision() {
		return decision;
	}

	public void setDecision(Decision_Dmn decision) {
		this.decision = decision;
	}

	public InputData_Dmn getInputData() {
		return inputData;
	}

	public void setInputData(InputData_Dmn inputData) {
		this.inputData = inputData;
	}

	@Override
	public String toString() {
		return "Requirement [" + decision + ", " + inputData + "]";
	}

}
