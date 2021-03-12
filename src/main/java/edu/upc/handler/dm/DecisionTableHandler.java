package edu.upc.handler.dm;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import edu.upc.entities.dm.DecisionTable_Dmn;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.Input_Dmn;
import edu.upc.entities.dm.Output_Dmn;
import edu.upc.entities.dm.Requirement;
import edu.upc.parserutils.dm.TypeRef_Table;

public class DecisionTableHandler {
	private LinkedHashMap<String, Requirement> requirements;

	public DecisionTableHandler(LinkedHashMap<String, Requirement> requirements) throws IOException {
		this.requirements = requirements;
	}

	public LinkedHashMap<String, DecisionTable_Dmn> process(LinkedHashMap<String, Decision_Dmn> decisionList)
			throws IOException {
		// Extract empty DecisionTable without rules
		LinkedHashMap<String, DecisionTable_Dmn> decisionTableList = new LinkedHashMap<String, DecisionTable_Dmn>();
		decisionTableList = extractEmptyDecisionTable(decisionList, decisionTableList);
		return decisionTableList;
	}

	private LinkedHashMap<String, DecisionTable_Dmn> extractEmptyDecisionTable(
			LinkedHashMap<String, Decision_Dmn> decisionList,
			LinkedHashMap<String, DecisionTable_Dmn> decisionTableList) throws IOException {
		for (Entry<String, Decision_Dmn> decision : decisionList.entrySet()) {
			String outputName = decision.getValue().getDrgElement().getName();
			Output_Dmn output = new Output_Dmn(outputName, outputName.replaceAll("[^A-Za-z0-9]", "").toLowerCase(),
					TypeRef_Table.STRING);
			DecisionTable_Dmn decisionTable = createNewDecisionTable(decision.getValue(), null, output);
			decisionTable = createInputsFromInputData(decisionTable);
			if (decisionTable.getInputs().size() > 0)
				decisionTableList.put(decisionTable.getId(), decisionTable);
		}
		return decisionTableList;
	}

	private DecisionTable_Dmn createInputsFromInputData(DecisionTable_Dmn decisionTable) {
		LinkedHashMap<String, Input_Dmn> inputs = new LinkedHashMap<String, Input_Dmn>();
		for (Entry<String, Requirement> requirement : requirements.entrySet()) {
			if (decisionTable.getName().equals(requirement.getValue().getDecision().getDrgElement().getName())) {
				String id = requirement.getValue().getInputData().getId();
				String name = requirement.getValue().getInputData().getDrgElement().getName();
				String expression = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();// + "_" + id;
				TypeRef_Table typeRef = TypeRef_Table.STRING;
				Input_Dmn input = new Input_Dmn(id, name, expression, typeRef);
				inputs.put(input.getId(), input);
			}
		}
		decisionTable.setInputs(inputs);
		return decisionTable;
	}

	private DecisionTable_Dmn createNewDecisionTable(Decision_Dmn decision, LinkedHashMap<String, Input_Dmn> inputs,
			Output_Dmn output) {

		DecisionTable_Dmn decisionTable = new DecisionTable_Dmn(decision.getId(), decision.getDrgElement().getName());
		decisionTable.setInputs(inputs);
		decisionTable.setOutput(output);
		decisionTable.setDecision(decision);
		System.out.println("-----------------------------------");
		System.out.println("DECISIONTABLE");
		System.out.println("id: " + decisionTable.getId() + "\nName: " + decisionTable.getName());
		System.out.println("HEADERS:");
		if (inputs != null) {
			for (Entry<String, Input_Dmn> input_Dmn : decisionTable.getInputs().entrySet()) {
				System.out.println("Input: [(" + input_Dmn.getKey() + ") " + input_Dmn.getValue().getName() + "]");
			}
		}

		System.out.println("Output:[" + output.getName() + "]");
		System.out.println("-----------------------------------");
		return decisionTable;
	}
}
