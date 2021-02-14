package edu.upc.handler.dm;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.entities.dm.Requirement;

public class RequirementHandler {
	private LinkedHashMap<String, Requirement> requirementList;

	public RequirementHandler() throws IOException {
		this.requirementList = new LinkedHashMap<String, Requirement>();
	}

	public LinkedHashMap<String, Requirement> process(LinkedHashMap<String, InputData_Dmn> inputDataList) {
		for (Entry<String, InputData_Dmn> inputData : inputDataList.entrySet()) {
			if (!inputData.getKey().equals(inputData.getValue().getDecision().getId()) 
					&& !inputData.getValue()
					.getDrgElement().getName().equals(inputData.getValue().getDecision().getDrgElement().getName())) {
				Requirement requirement = new Requirement(inputData.getValue(), inputData.getValue().getDecision());
				requirementList.put(inputData.getKey(), requirement);
				System.out.println(
						"> Requirement: " + inputData.getKey() + " " + inputData.getValue().getDrgElement().getName()
								+ " ---> " + inputData.getValue().getDecision().getId() + " - "
								+ inputData.getValue().getDecision().getDrgElement().getName());
			}
		}
		removeDuplicateRequirements();
		changeInputToDecision();
		sortRequirementList();
		inputDataList.clear();
		for (Entry<String, Requirement> req : requirementList.entrySet()) {
			inputDataList.put(req.getValue().getInputData().getId(), req.getValue().getInputData());
		}
		return requirementList;
	}

	private void removeDuplicateRequirements() {
		LinkedHashMap<String, Requirement> reqsTmp = new LinkedHashMap<String, Requirement>();
		for (Entry<String, Requirement> req : requirementList.entrySet()) {
			Boolean found = false;
			for (Entry<String, Requirement> reqTmp : reqsTmp.entrySet()) {
				String inName = req.getValue().getInputData().getDrgElement().getName().toLowerCase().trim();
				String inTmpName = reqTmp.getValue().getInputData().getDrgElement().getName().toLowerCase().trim();
				String decName = req.getValue().getDecision().getDrgElement().getName().toLowerCase().trim();
				String decTmpName = reqTmp.getValue().getDecision().getDrgElement().getName().toLowerCase().trim();
				if ((inName.contains(inTmpName) || inTmpName.contains(inName))
						&& (decName.contains(decTmpName) || decTmpName.contains(decName))) {
					found = true;
					break;
				}
			}
			if (!found)
				reqsTmp.put(req.getKey(), req.getValue());
		}
		requirementList.clear();
		requirementList.putAll(reqsTmp);
	}

	private void sortRequirementList() {
		Map<String, Requirement> result = requirementList.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));
		requirementList.clear();
		requirementList.putAll(result);
	}

	private void changeInputToDecision() {
		LinkedHashMap<String, Requirement> listTmp = new LinkedHashMap<String, Requirement>();
		listTmp.putAll(requirementList);
		for (Entry<String, Requirement> dependency : requirementList.entrySet()) {
			if (!dependency.getKey().contains("input")) {
				String input = dependency.getValue().getInputData().getDrgElement().getName().trim().toLowerCase();
				for (Entry<String, Requirement> dependency2 : listTmp.entrySet()) {
					String decision2 = dependency2.getValue().getDecision().getDrgElement().getName().trim()
							.toLowerCase();
					if (input.contains(decision2)) {
						dependency.getValue().getInputData()
								.setDrgElement(dependency2.getValue().getDecision().getDrgElement());
						break;
					}
				}
			}
		}
	}

}
