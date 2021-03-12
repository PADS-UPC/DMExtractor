package edu.upc.parserutils.dm;

public enum TypeRef_Table {
	STRING("string"), 
	BOOLEAN("boolean"), 
	NUMERIC("double");

	private final String description;

	TypeRef_Table(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

}
