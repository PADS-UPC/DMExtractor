package edu.upc.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.upc.entities.Patient;
import edu.upc.entities.Predicate;
import edu.upc.entities.PredicateArgument;
import edu.upc.entities.Token;
import edu.upc.freelingutils.FilesUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;

public class PatientHandler {
	private LinkedHashMap<String, Patient> patientsList;
	private List<Tree> trees;
	private LinkedHashMap<String, Token> tokens;
	private LinkedHashMap<String, Predicate> predicates;
	private ArrayList<String> posTagToLimitPatientName;
	private ArrayList<String> determinerToLimitPatientName;
	private String beginToken, endToken;

	public PatientHandler(List<Tree> trees, LinkedHashMap<String, Token> tokens,
			LinkedHashMap<String, Predicate> predicates) throws IOException {
		super();
		this.trees = trees;
		this.tokens = tokens;
		this.predicates = predicates;
		this.patientsList = new LinkedHashMap<String, Patient>();

		posTagToLimitPatientName = new ArrayList<String>();
		posTagToLimitPatientName = DmnFreelingUtils.readPatternFile(FilesUrl.POSTAG_TO_LIMIT_PATIENT_NAME.toString());

		determinerToLimitPatientName = new ArrayList<String>();
		determinerToLimitPatientName = DmnFreelingUtils
				.readPatternFile(FilesUrl.DETERMINER_TO_LIMIT_PATIENT_NAME.toString());

		fillPatientsFromPredicate();
		delimitePatientsNameLength();
		//removePatientsWithNullText();
	}

	private void fillPatientsFromPredicate() {
		Integer count = 1;
		for (Entry<String, Predicate> predicate : predicates.entrySet()) {
			if (predicate.getValue().getA1PlusArguments().size() > 0) {
				Patient patient = null;
				String completeText = "";
				Integer begin = 0;
				Integer end = 0;
				boolean found = false;
				String patientId = null;
				for (Entry<String, PredicateArgument> argument : predicate.getValue().getA1PlusArguments().entrySet()) {
					completeText += argument.getValue().getWords().toLowerCase() + " ";
					if (!found) {
						patientId = argument.getValue().getHead_token();
						begin = tokens.get(argument.getValue().getFrom()).getBegin();
						end = tokens.get(argument.getValue().getTo()).getEnd();
						found = true;
					} else {
						if (end <= tokens.get(argument.getValue().getTo()).getEnd())
							end = tokens.get(argument.getValue().getTo()).getEnd();
					}
				}
				patient = new Patient(patientId, completeText, completeText, begin, end);
				patientsList.put(patientId, patient);
				count++;
			}
		}
	}

	private void delimitePatientsNameLength() {
		System.out.println("--> Apply patterns to get Patient's short name");
		for (Entry<String, Patient> patient : patientsList.entrySet()) {
			beginToken = null;
			endToken = null;
			String text = "";
			String patientToken = patient.getKey();
			if (posTagToLimitPatientName.contains(tokens.get(patientToken).getPos())
					|| determinerToLimitPatientName.contains(tokens.get(patientToken).getForm().toLowerCase())) {
				beginToken = patientToken;
				endToken = patientToken;
				findSubjectInSubtreesAndMakePatientName(patientToken); // TODO send exactly tree
			}
			if (beginToken != null) {
				String sentenceNumber = beginToken.replace(".", " ").split(" ", 2)[0];
				Integer initNumber = Integer.parseInt(beginToken.replace(".", " ").split(" ", 2)[1]);
				Integer endNumber = Integer.parseInt(endToken.replace(".", " ").split(" ", 2)[1]);
				for (int i = initNumber; i <= endNumber; i++) {
					text += tokens.get(sentenceNumber + "." + i).getForm() + " ";
				}
			}

			if (!text.trim().isEmpty()) {
				patient.getValue().setShortText(text.trim());
				patient.getValue().setBegin(tokens.get(beginToken).getBegin());
				patient.getValue().setEnd(tokens.get(endToken).getEnd());
			} else {
				patient.getValue().setShortText(null);
				patient.getValue().setBegin(null);
				patient.getValue().setEnd(null);
			}
		}
	}

	private void findSubjectInSubtreesAndMakePatientName(String patientToken) {
		if (determinerToLimitPatientName.contains(tokens.get(patientToken).getForm().toLowerCase())) {
			endToken = patientToken;
		}
		if (posTagToLimitPatientName.contains(tokens.get(patientToken).getPos())) {
			ArrayList<String> list = new ArrayList<String>();
			posTagToLimitPatientName.forEach(action -> list.add(action));
			String stringWithOrBarFromList = DmnFreelingUtils.joinStringsListWithOrSeparator(list);
			TregexPattern pattern = TregexPattern.compile("/" + DmnFreelingUtils.separator + stringWithOrBarFromList
					+ DmnFreelingUtils.separator + "/=result > /" + DmnFreelingUtils.separator + patientToken
					+ DmnFreelingUtils.separator + "/");
			for (int i = 0; i < trees.size(); i++) {
				TregexMatcher matcher = pattern.matcher(trees.get(i));
				while (matcher.findNextMatchingNode()) {
					Tree tNounResult = matcher.getNode("result");
					String patientToken2 = DmnFreelingUtils.getTokenFromNode(tNounResult.label().value());
					if (DmnFreelingUtils.isTokenALessThanTokenB(patientToken, patientToken2))
						endToken = patientToken2;
					else {
						beginToken = patientToken2;
						endToken = patientToken;
					}
					findSubjectInSubtreesAndMakePatientName(patientToken2);
					i = trees.size();
				}
			}
		}
	}

	private void removePatientsWithNullText() {
		LinkedHashMap<String, Patient> patientsListTmp = new LinkedHashMap<String, Patient>();
		for (Entry<String, Patient> patient : patientsList.entrySet()) {
			if (patient.getValue().getShortText() != null) {
				patientsListTmp.put(patient.getKey(), patient.getValue());
			}
		}
		patientsList = new LinkedHashMap<String, Patient>();
		patientsList.putAll(patientsListTmp);
	}

	public LinkedHashMap<String, Patient> getPatientsList() {
		return patientsList;
	}

	public void setPatientsList(LinkedHashMap<String, Patient> patientsList) {
		this.patientsList = patientsList;
	}

}
