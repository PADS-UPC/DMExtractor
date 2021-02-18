package edu.upc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.DmnModelElementInstance;
import org.camunda.bpm.model.dmn.instance.InformationRequirement;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputData;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.camunda.bpm.model.dmn.instance.Text;
import edu.upc.entities.dm.DecisionTable_Dmn;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.InputEntry_Rule;
import edu.upc.entities.dm.Input_Dmn;
import edu.upc.entities.dm.Requirement;
import edu.upc.entities.dm.Rule_Table;
import edu.upc.freelingutils.dm.DmnFoldersUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;
import edu.upc.freelingutils.dm.TypeRef_Table;

import java.util.*;
import java.util.Map.Entry;

public class Drd {

	DmnModelInstance modelInstance;// = Dmn.createEmptyModel();
	private LinkedHashMap<String, DecisionTable_Dmn> decisionTableList;
	private LinkedHashMap<String, Decision_Dmn> decisionList;
	private LinkedHashMap<String, Requirement> requirementList;

	public Drd(LinkedHashMap<String, Decision_Dmn> decisionList,
			LinkedHashMap<String, DecisionTable_Dmn> decisionTableList,
			LinkedHashMap<String, Requirement> requirementList) {
		this.decisionList = decisionList;
		this.decisionTableList = decisionTableList;
		this.requirementList = requirementList;
	}

	public String generateDrd() throws IOException {
		if (decisionTableList == null)
			return "";

		String dmnFileName = DmnFoldersUrl.INPUT_FOLDER + "/empty.dmn";
		String emptyXml = DmnFreelingUtils.readFile(dmnFileName);
		InputStream targetStream = new ByteArrayInputStream(emptyXml.getBytes());
		modelInstance = Dmn.readModelFromStream(targetStream);

		Definitions definitions = modelInstance.getModelElementById("dmn_1");
		modelInstance.setDocumentElement(definitions);
		modelInstance.setDefinitions(definitions);

		// create decisions
		Integer ruleId = 0;
		for (Entry<String, Decision_Dmn> decision_Dmn : decisionList.entrySet()) {
			ruleId++;
			// Don not create decisions without DecisionTable
			if (decisionTableList.containsKey(decision_Dmn.getKey())) {
				// Create Decision
				Decision decision = createElement(definitions, "Decision_" + decision_Dmn.getKey().replace(".", "-"),
						decision_Dmn.getValue().getDrgElement().getName(), Decision.class);
				// Add DecisionTable to Decision
				DecisionTable_Dmn dtdmn = decisionTableList.get(decision_Dmn.getKey());
				if (dtdmn != null) {
					DecisionTable decisionTable = modelInstance.newInstance(DecisionTable.class);
					decisionTable.setId("DecisionTable_" + dtdmn.getId().replace(".", "-"));
					// decisionTable.setHitPolicy(HitPolicy.COLLECT);
					decision.addChildElement(decisionTable);
					// Add Inputs to DecisionTable
					for (Entry<String, Input_Dmn> inputData : dtdmn.getInputs().entrySet()) {
						Input input = modelInstance.newInstance(Input.class);
						input.setId("InputClause_" + inputData.getKey().replace(".", "-"));
						input.setLabel(inputData.getValue().getName());
						decisionTable.addChildElement(input);
						InputExpression inputExpression = modelInstance.newInstance(InputExpression.class);
						inputExpression.setId("LiteralExpression_" + inputData.getKey().replace(".", "-"));
						Text inputExpressionText = modelInstance.newInstance(Text.class);
						// inputExpressionText.setTextContent(inputData.getValue().getExpression());
						inputExpressionText.setTextContent(inputData.getValue().getExpression());// .getName().replaceAll("\\s+",
																									// "").toLowerCase().replace("'",
																									// ""));
						inputExpression.setText(inputExpressionText);
						inputExpression.setTypeRef(inputData.getValue().getTypeRef().toString());
						input.setInputExpression(inputExpression);
					}
					// Add output to DecisionTable
					Output output = modelInstance.newInstance(Output.class);
					output.setName(dtdmn.getOutput().getName().replaceAll("\\s+", "").toLowerCase().replace("'", ""));
					output.setLabel(dtdmn.getOutput().getName());
					output.setTypeRef(dtdmn.getOutput().getTypeRef().toString());
					decisionTable.addChildElement(output);
					// Add rules to DecisionTable
					Integer count = 0;
					if (dtdmn.getRules() != null) {
						for (int i = 0; i < dtdmn.getRules().size(); i++) {
							ruleId = ruleId + i;
							Rule_Table ruleTable = dtdmn.getRules().get(i);
							Rule rule = modelInstance.newInstance(Rule.class);
							rule.setId("rule-" + ruleId);
							for (Entry<String, Input_Dmn> input_Dmn : dtdmn.getInputs().entrySet()) {
								String inputEntryText = null;
								for (Entry<String, InputEntry_Rule> inputEntryRule : ruleTable.getInputEntries()
										.entrySet()) {
									String inputName = input_Dmn.getValue().getName().toLowerCase().trim();
									if (inputEntryRule.getValue().getInput() != null) {
										String inputNameFromEntry = inputEntryRule.getValue().getInput().getName()
												.toLowerCase().trim();
										if (inputName.contains(inputNameFromEntry)
												|| inputNameFromEntry.contains(inputName)) {
											inputEntryText = getTextContent(inputEntryRule.getValue().getText(),
													inputEntryRule.getValue().getInput().getTypeRef(),
													inputEntryRule.getValue().getNegation());

											String id = "InputClause_" + input_Dmn.getKey().replace(".", "-");
											decisionTable.getInputs()
													.forEach(input -> changeInputTypeRef(input, id, inputEntryRule));
										}
									}
								}
								InputEntry inputEntry = modelInstance.newInstance(InputEntry.class);
								inputEntry.setId("inputEntry_" + ruleId + "_" + count);

								Text text = modelInstance.newInstance(Text.class);
								text.setTextContent(inputEntryText);
								inputEntry.setText(text);

								rule.addChildElement(inputEntry);
								count++;

							}

							Text text = modelInstance.newInstance(Text.class);
							OutputEntry outputEntry = modelInstance.newInstance(OutputEntry.class);
							String textContent = "";
							if (ruleTable.getOutputEntry() != null) {
								textContent = ruleTable.getOutputEntry().getText();
								textContent = getTextContent(ruleTable.getOutputEntry().getText(),
										dtdmn.getOutput().getTypeRef(), ruleTable.getOutputEntry().getNegation());
								text.setTextContent(textContent);
								outputEntry.setId("outentry_" + ruleId + "_" + i);
								outputEntry.setText(text);
							}
							rule.addChildElement(outputEntry);
							decisionTable.addChildElement(rule);
						}
					}
				}
			}
		}

		// Make InputDecision
		for (Entry<String, Requirement> requirement : requirementList.entrySet()) {
			String id = "InputData_" + requirement.getValue().getInputData().getId().replace(".", "-");
			String name = requirement.getValue().getInputData().getDrgElement().getName();
			System.out.println(id + " -- " + name);
			InputData inputData = modelInstance.newInstance(InputData.class);
			inputData.setId(id);
			inputData.setName(name);
			definitions.getDomElement().appendChild(inputData.getDomElement());
		}

		// Make requirements
		ArrayList<Decision> decisions = (ArrayList<Decision>) modelInstance.getModelElementsByType(Decision.class);
		if (decisions.size() > 0) {
			for (int i = 0; i < decisions.size(); i++) {
				String decisionToken = decisions.get(i).getId().replace("Decision_", "").replace("-", ".");
				for (Entry<String, Input_Dmn> input_Dmn : decisionTableList.get(decisionToken).getInputs().entrySet()) {
					String inputName = input_Dmn.getValue().getName();
					// Create Requirements
					for (int j = i + 1; j < decisions.size(); j++) {
						String decicionNameTmp = decisions.get(j).getName();
						if (inputName.contains(decicionNameTmp) || decicionNameTmp.contains(inputName)) {
							InformationRequirement ir = createElement(decisions.get(i), null, null,
									InformationRequirement.class);
							ir.setRequiredDecision(decisions.get(j));
						}
					}
				}

			}
		}
		Dmn.validateModel(modelInstance);
		String xmlString = Dmn.convertToString(modelInstance);
		xmlString = xmlString.replace("http://www.omg.org/spec/DMN/20151101/dmn.xsd",
				"https://www.omg.org/spec/DMN/20191111/MODEL/");
		return xmlString;
	}

	private String getTextContent(String text, TypeRef_Table typeRef, Boolean negation) {
		if (typeRef.equals(TypeRef_Table.BOOLEAN)) {
			if (negation) {
				if (text.equals("true"))
					text = "false";
				else
					text = "true";
			}
		} else if (typeRef.equals(TypeRef_Table.NUMERIC)) {
			if (negation)
				text = "not(" + text + ")";
		} else {
			text = "\"" + text + "\"";
			if (negation)
				text = "not(" + text + ")";
		}
		return text;
	}

	private Object changeInputTypeRef(Input input, String id, Entry<String, InputEntry_Rule> inputEntryRule) {
		if (input.getAttributeValue("id").equals(id)) {
			input.getInputExpression().setTypeRef(inputEntryRule.getValue().getInput().getTypeRef().toString());
		}
		return input;
	}

	public void makeXml() {

		modelInstance = Dmn.createEmptyModel();

		Definitions definitions = modelInstance.newInstance(Definitions.class);
		definitions.setNamespace("http://camunda.org/schema/1.0/dmn");
		definitions.setName("definitions");
		definitions.setId("definitions");
		modelInstance.setDefinitions(definitions);

		Decision decision = modelInstance.newInstance(Decision.class);
		decision.setId("testGenerated");
		decision.setName("generationtest");
		definitions.addChildElement(decision);

		// validate the model
		Dmn.validateModel(modelInstance);

		// Turn into string
		String xmlString = Dmn.convertToString(modelInstance);
		System.out.println(xmlString);
	}

	protected <T extends DmnModelElementInstance> T createElement(DmnModelElementInstance parentElement, String id,
			String name, Class<T> elementClass) {
		T element = modelInstance.newInstance(elementClass);
		if (id != null)
			element.setAttributeValue("id", id, true);
		// id = getRandomText();
		if (name != null)
			element.setAttributeValue("name", name, true);
		// name = "obect_" + id;

		parentElement.addChildElement(element);
		return element;
	}
}
