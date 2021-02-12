package edu.upc.freelingutils;

public enum FoldersUrl {
	FREELING_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/freeling/"),
	PATTERN_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/input/patternsbpmn/"),
	INPUT_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/input/"),
	OUTPUT_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/output/"),
	TREE_FOLDER(System.getProperty("user.home") + "/doc2dmnutils/en/output/tree/");

	
	private String description;

	private FoldersUrl(String description) {
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
