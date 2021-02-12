package edu.upc.freelingutils.dm;

public enum DmnFoldersUrl {
	FREELING_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/freeling/"),
	PATTERN_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/input/patterns/"),
	INPUT_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/input/"),
	OUTPUT_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/output/"),
	TREE_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/output/tree/"),
	TUPLE_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/output/tuple/");

	
	private String description;

	private DmnFoldersUrl(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description=description;
	}

	@Override
	public String toString() {
		return description;
	}
}
