package edu.upc.entities.dm;

import edu.upc.entities.Action;

public class Decision_Dmn {

	String id;
	DrgElement drgElement;
	Action action;

	public Decision_Dmn(String id, DrgElement drgElement, Action action) {
		this.id = id;
		this.drgElement = drgElement;
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
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

	@Override
	public String toString() {
		return "DecisionEntity [id=" + id + ", drgElement=" + drgElement + ", action=" + action + "]";
	}

}
