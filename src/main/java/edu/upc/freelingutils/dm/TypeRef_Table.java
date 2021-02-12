package edu.upc.freelingutils.dm;

public enum TypeRef_Table {
	STRING("string"), 
	BOOLEAN("boolean"), 
	INTEGER("integer"),
	DOUBLE("double");

	private final String description;

	TypeRef_Table(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

}
