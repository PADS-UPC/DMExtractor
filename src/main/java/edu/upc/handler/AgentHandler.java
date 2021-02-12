package edu.upc.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.upc.entities.Agent;
import edu.upc.entities.Entity;
import edu.upc.entities.Predicate;
import edu.upc.entities.Token;
import edu.upc.freelingutils.FilesUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;

public class AgentHandler {
	private LinkedHashMap<String, Agent> agentList;
	private List<Tree> trees;
	private LinkedHashMap<String, Token> tokens;
	private LinkedHashMap<String, Predicate> predicates;
	private ArrayList<String> posTagToLimitShortAgentName;
	private ArrayList<String> prepositionToLimitAgentName;
	private String beginToken, endToken;

	public AgentHandler(List<Tree> trees, LinkedHashMap<String, Token> tokens,
			LinkedHashMap<String, Predicate> predicates, LinkedHashMap<String, Entity> entities) throws IOException {
		super();
		this.trees = trees;
		this.tokens = tokens;
		this.predicates = predicates;
		this.agentList = new LinkedHashMap<String, Agent>();

		posTagToLimitShortAgentName = new ArrayList<String>();
		posTagToLimitShortAgentName = DmnFreelingUtils.readPatternFile(FilesUrl.POSTAG_TO_LIMIT_AGENT_NAME.toString());

		prepositionToLimitAgentName = new ArrayList<String>();
		prepositionToLimitAgentName = DmnFreelingUtils
				.readPatternFile(FilesUrl.PREPOSITION_TO_LIMIT_AGENT_NAME.toString());

		fillAgentsFromPredicate();
		//// To DMN it is not necessary
		//// removeEqualAgentInOtherMentions_coreference();

		delimiteAgentsNameLength();
		agentList = removeAgentsWithNullShortName();
	}

	private void delimiteAgentsNameLength() {
		System.out.println("--> Apply patterns to get Patient's short name");
		for (Entry<String, Agent> agent : agentList.entrySet()) {
			beginToken = null;
			endToken = null;
			String text = "";
			String agentToken = agent.getKey();
			if (posTagToLimitShortAgentName.contains(tokens.get(agentToken).getPos())) {
				beginToken = agentToken;
				endToken = agentToken;
				findSubjectInSubtreesAndMakeAgentName(agentToken); // TODO send exactly tree
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
				agent.getValue().setShortText(text.trim());
				agent.getValue().setBegin(tokens.get(beginToken).getBegin());
				agent.getValue().setEnd(tokens.get(endToken).getEnd());
			} else {
				agent.getValue().setShortText(null);
				agent.getValue().setBegin(null);
				agent.getValue().setEnd(null);
			}
		}
	}

	private void findSubjectInSubtreesAndMakeAgentName(String agentToken) {

		if (posTagToLimitShortAgentName.contains(tokens.get(agentToken).getPos())
				|| prepositionToLimitAgentName.contains(tokens.get(agentToken).getLemma())) {
			ArrayList<String> list = new ArrayList<String>();
			posTagToLimitShortAgentName.forEach(action -> list.add(action));
			String stringWithOrBarFromList = DmnFreelingUtils.joinStringsListWithOrSeparator(list);
			TregexPattern pattern = TregexPattern.compile("/" + DmnFreelingUtils.separator + stringWithOrBarFromList
					+ DmnFreelingUtils.separator + "/=result > /" + DmnFreelingUtils.separator + agentToken
					+ DmnFreelingUtils.separator + "/");
			for (int i = 0; i < trees.size(); i++) {
				TregexMatcher matcher = pattern.matcher(trees.get(i));
				while (matcher.findNextMatchingNode()) {
					Tree tNounResult = matcher.getNode("result");
					String patientToken2 = DmnFreelingUtils.getTokenFromNode(tNounResult.label().value());
					if (DmnFreelingUtils.isTokenALessThanTokenB(agentToken, patientToken2))
						endToken = patientToken2;
					else {
						beginToken = patientToken2;
						endToken = agentToken;
					}
					findSubjectInSubtreesAndMakeAgentName(patientToken2);
					i = trees.size();
				}
			}
		}
	}

	private void fillAgentsFromPredicate() {
		for (Entry<String, Predicate> predicate : predicates.entrySet()) {
			if (predicate.getValue().getArgumentA0() != null && !predicate.getValue().getHead_token()
					.equals(predicate.getValue().getArgumentA0().getHead_token())) {
				String head_token = predicate.getValue().getArgumentA0().getHead_token();
				String words = predicate.getValue().getArgumentA0().getWords().toLowerCase();
				String fromToken = predicate.getValue().getArgumentA0().getFrom();
				String toToken = predicate.getValue().getArgumentA0().getTo();
				Agent agent = new Agent(head_token, words, tokens.get(fromToken).getBegin(),
						tokens.get(toToken).getEnd());
				agentList.put(head_token, agent);
			}
		}
	}

	private LinkedHashMap<String, Agent> removeAgentsWithNullShortName() {
		LinkedHashMap<String, Agent> agentsListTmp = new LinkedHashMap<String, Agent>();
		for (Entry<String, Agent> agent : agentList.entrySet()) {
			if (agent.getValue().getShortText() != null && !agent.getValue().getShortText().equals("")) {
				agentsListTmp.put(agent.getValue().getId(), agent.getValue());
			}
		}
		return agentsListTmp;
	}

	public LinkedHashMap<String, Agent> getAgentsList() {
		return agentList;
	}

	public void setAgentsList(LinkedHashMap<String, Agent> agentsList) {
		this.agentList = agentsList;
	}

}
