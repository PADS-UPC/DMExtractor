package edu.upc.entities.dm;

import edu.upc.freelingutils.dm.TypeRef_Table;

public class Input_Dmn {

	private String id;
	private String name; //Obesity level
	private String expression; // obesitylevel
	private TypeRef_Table typeRef; //string, boolean

	public Input_Dmn(String id, String name, String expression, TypeRef_Table typeRef) {
		this.id = id;
		this.name = name;
		this.expression = expression;
		this.typeRef = typeRef;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public TypeRef_Table getTypeRef() {
		return typeRef;
	}

	public void setTypeRef(TypeRef_Table typeRef) {
		this.typeRef = typeRef;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Input_Dmn [id=" + id + ", name=" + name + ", expression=" + expression + ", typeRef=" + typeRef + "]";
	}

}
