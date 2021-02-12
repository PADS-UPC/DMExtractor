package edu.upc.freelingutils;

public enum FilesUrl {
	FREELING_CACHE_FILE(FoldersUrl.FREELING_FOLDER + "cache/freeling-cache"),
	FREEELING_NOUN_CONFIG_FILE(FoldersUrl.FREELING_FOLDER + "config/pred-nom.dat"),
	FREEELING_NOUN_TO_ADD_CONFIG_FILE(FoldersUrl.FREELING_FOLDER + "config/pred-nom-add.dat"),
	TEMPORAL_TREE(FoldersUrl.TREE_FOLDER + "temporal.trx"),
	EVENT_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "event.txt"),
	CONDITION_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "condition.txt"),
	CONDITION_SIMPLE_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "conditionsimple.txt"),
	REMOVE_ACTIVITY_FILE(FoldersUrl.PATTERN_FOLDER + "removeactivity.txt"),
	ACTIVITY_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "activity.txt"),
	SEQUENCE_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "sequence.txt"),
	SEQUENCE_BETWEEN_SENTENCES_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "sequencesentence.txt"),
	WEAK_SEQUENCE_BETWEEN_SENTENCES_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "weaksequencesentence.txt"),
	CONFLICT_BETWEEN_SENTENCES_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "conflictsentences.txt"),
	REMOVE_CONFLICT_BETWEEN_SENTENCES_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "removeconflictsentence.txt"),
	CONFLICT_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "conflict.txt"),
	WEAK_VERBS_PATTERNS_FILE(FoldersUrl.PATTERN_FOLDER + "weakverbs.txt"),
	TEXTSERVER_CREDENTIALS(System.getProperty("user.home") + "/.textserver-credentials"),
	//
	PREPOSITION_TO_LIMIT_AGENT_NAME(FoldersUrl.PATTERN_FOLDER + "prepositionToLimitAgentName.txt"),
	POSTAG_TO_LIMIT_AGENT_NAME(FoldersUrl.PATTERN_FOLDER + "posTagToLimitAgentName.txt"),
	POSTAG_TO_LIMIT_PATIENT_NAME(FoldersUrl.PATTERN_FOLDER + "posTagToLimitPatientName.txt"),
	DETERMINER_TO_LIMIT_PATIENT_NAME(FoldersUrl.PATTERN_FOLDER + "determinerToLimitPatientName.txt"),
	PATIENT_PRONOUNS(FoldersUrl.PATTERN_FOLDER + "patientPronouns.txt"),
	LIMITS_TO_MAKE_CONDITIONAL_PHRASE(FoldersUrl.PATTERN_FOLDER + "limitsToMakeConditionalPhrase.txt")
	
	;

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
