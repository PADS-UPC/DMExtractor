package edu.upc.entities.dm;

import edu.upc.parserutils.ActionType;
import edu.upc.parserutils.dm.TypeRef_Table;

public class DrgElement {

	String token;
	String name;
	ActionType type;
	TypeRef_Table typeRef;

	public DrgElement(String token, String name, ActionType type) {
		super();
		this.token = token;
		this.name = name;
		this.type = type;
		typeRef = TypeRef_Table.STRING;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ActionType getType() {
		return type;
	}

	public TypeRef_Table getTypeRef() {
		return typeRef;
	}

	public void setTypeRef(TypeRef_Table typeRef) {
		this.typeRef = typeRef;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[token=" + this.token + ",name=" + this.name + ",Type=" + this.type + "]";
	}
}
