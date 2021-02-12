package edu.upc.entities.dm;

import edu.upc.entities.Action;

public class InputData_Dmn {
	String id;
	Decision_Dmn decision;
	DrgElement drgElement;
	Action action;

	public InputData_Dmn(String id, Decision_Dmn decision, DrgElement drgElement, Action action) {
		super();
		this.id = id;
		this.drgElement = drgElement;
		this.decision = decision;
		this.action = action;
	}

	public Decision_Dmn getDecision() {
		return decision;
	}

	public void setDecision(Decision_Dmn decision) {
		this.decision = decision;
	}

	public DrgElement getDrgElement() {
		return drgElement;
	}

	public void setDrgElement(DrgElement drgElement) {
		this.drgElement = drgElement;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "InputData_Dmn [id=" + id + ", decision=" + decision + ", drgElement=" + drgElement + ", action="
				+ action + "]";
	}

}
