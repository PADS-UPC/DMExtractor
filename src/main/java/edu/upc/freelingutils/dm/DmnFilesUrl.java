package edu.upc.freelingutils.dm;

public enum DmnFilesUrl {
	FREELING_CACHE_FILE(DmnFoldersUrl.FREELING_FOLDER + "cache/freeling-cache"),
	FREEELING_NOUN_CONFIG_FILE(DmnFoldersUrl.FREELING_FOLDER + "config/pred-nom.dat"),
	FREEELING_NOUN_TO_ADD_CONFIG_FILE(DmnFoldersUrl.FREELING_FOLDER + "config/pred-nom-add.dat"),
	TEMPORAL_TREE(DmnFoldersUrl.TREE_FOLDER + "temporal.trx"),
	TEXTSERVER_CREDENTIALS(System.getProperty("user.home") + "/.textserver-credentials"),
	DECISION(DmnFoldersUrl.PATTERN_FOLDER+ "decision.txt"),
	DECISION_VERBLIST(DmnFoldersUrl.PATTERN_FOLDER+ "decision_verbs.txt"),
	DECISION_PATTERN(DmnFoldersUrl.PATTERN_FOLDER+ "decision_pattern.txt"),
	INPUTENTRY_UNDERACTION_PATTERN(DmnFoldersUrl.PATTERN_FOLDER+ "inputEntry_underAction_pattern.txt"),
	INPUTDATA_UNDERINPUTDATA_PATTERN(DmnFoldersUrl.PATTERN_FOLDER+ "inputData_underInputData_pattern.txt"),
	DECISIONTABLE_RULES(DmnFoldersUrl.PATTERN_FOLDER+ "decisionTable_rules.txt"),
	//DECISIONTABLE_PATTERN(DmnFoldersUrl.PATTERN_FOLDER+ "decisionTable_pattern.txt"),
	POSTAG_FOR_ENTRYNAME(DmnFoldersUrl.PATTERN_FOLDER + "posTagForEntryName.txt"),
	PREPOSITIONS_FOR_ENTRYNAME(DmnFoldersUrl.PATTERN_FOLDER + "prepositionsForEntryName.txt"),
	POSTAG_FOR_INPUTDATANAME(DmnFoldersUrl.PATTERN_FOLDER + "posTagForInputDataName.txt"),
	PREPOSITIONS_FOR_INPUTDATANAME(DmnFoldersUrl.PATTERN_FOLDER + "prepositionsForInputDataName.txt"),
	REMOVE_ACTIONS(DmnFoldersUrl.PATTERN_FOLDER + "removeActions.txt"),
	DAYS(DmnFoldersUrl.PATTERN_FOLDER + "days.txt"),
	MONTHS(DmnFoldersUrl.PATTERN_FOLDER + "months.txt"),
	YEARS(DmnFoldersUrl.PATTERN_FOLDER + "years.txt"),
	WEATHER(DmnFoldersUrl.PATTERN_FOLDER + "weather.txt")
	//
	;

	private String description;

	private DmnFilesUrl(String description) {
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
