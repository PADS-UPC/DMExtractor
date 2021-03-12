package edu.upc.parserutils;

public enum FilesUrl {
	FREELING_CACHE_FILE(FoldersUrl.FREELING_FOLDER + "cache/freeling-cache"),
	FREEELING_NOUN_CONFIG_FILE(FoldersUrl.FREELING_FOLDER + "config/pred-nom.dat"),
	FREEELING_NOUN_TO_ADD_CONFIG_FILE(FoldersUrl.FREELING_FOLDER + "config/pred-nom-add.dat"),
	TEMPORAL_TREE(FoldersUrl.TREE_FOLDER + "temporal.trx"),
	ACTIVITY_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER_BPMN + "activity.txt"),
	TEXTSERVER_CREDENTIALS(System.getProperty("user.home") + "/.textserver-credentials"),
	//
	PREPOSITION_TO_LIMIT_AGENT_NAME(FoldersUrl.PATTERN_FOLDER_BPMN + "prepositionToLimitAgentName.txt"),
	POSTAG_TO_LIMIT_AGENT_NAME(FoldersUrl.PATTERN_FOLDER_BPMN + "posTagToLimitAgentName.txt"),
	POSTAG_TO_LIMIT_PATIENT_NAME(FoldersUrl.PATTERN_FOLDER_BPMN + "posTagToLimitPatientName.txt"),
	DETERMINER_TO_LIMIT_PATIENT_NAME(FoldersUrl.PATTERN_FOLDER_BPMN + "determinerToLimitPatientName.txt");

	private String description;

	private FilesUrl(String description) {
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
