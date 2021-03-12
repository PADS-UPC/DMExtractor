package edu.upc.parserutils.dm;
import edu.upc.parserutils.FoldersUrl;

public enum DmnFilesUrl {
	TEMPORAL_TREE(FoldersUrl.TREE_FOLDER + "temporal.trx"),
	TEXTSERVER_CREDENTIALS(System.getProperty("user.home") + "/.textserver-credentials"),
	DECISION(FoldersUrl.PATTERN_FOLDER+ "decision.txt"),
	DECISION_VERBLIST(FoldersUrl.PATTERN_FOLDER+ "decision_verbs.txt"),
	DECISION_PATTERN(FoldersUrl.PATTERN_FOLDER+ "decision_pattern.txt"),
	INPUTENTRY_UNDERACTION_PATTERN(FoldersUrl.PATTERN_FOLDER+ "inputEntry_underAction_pattern.txt"),
	INPUTDATA_UNDERINPUTDATA_PATTERN(FoldersUrl.PATTERN_FOLDER+ "inputData_underInputData_pattern.txt"),
	DECISIONTABLE_RULES(FoldersUrl.PATTERN_FOLDER+ "decisionTable_rules.txt"),
	POSTAG_FOR_ENTRYNAME(FoldersUrl.PATTERN_FOLDER + "posTagForEntryName.txt"),
	PREPOSITIONS_FOR_ENTRYNAME(FoldersUrl.PATTERN_FOLDER + "prepositionsForEntryName.txt"),
	POSTAG_FOR_INPUTDATANAME(FoldersUrl.PATTERN_FOLDER + "posTagForInputDataName.txt"),
	PREPOSITIONS_FOR_INPUTDATANAME(FoldersUrl.PATTERN_FOLDER + "prepositionsForInputDataName.txt"),
	REMOVE_ACTIONS(FoldersUrl.PATTERN_FOLDER + "removeActions.txt"),
	DAYS(FoldersUrl.PATTERN_FOLDER + "days.txt"),
	MONTHS(FoldersUrl.PATTERN_FOLDER + "months.txt"),
	YEARS(FoldersUrl.PATTERN_FOLDER + "years.txt"),
	WEATHER(FoldersUrl.PATTERN_FOLDER + "weather.txt");

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
