package edu.upc.entities.dm;

import edu.upc.parserutils.dm.TypeRef_Table;

public class Output_Dmn {

	private String name;
	private String expression;
	private TypeRef_Table typeRef;

	public Output_Dmn(String name, String expression, TypeRef_Table typeRef) {
		super();
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

	@Override
	public String toString() {
		return "Output_Dmn [name=" + name + ", expression=" + expression + ", typeRef=" + typeRef + "]";
	}

}
