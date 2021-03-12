package edu.upc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;

import edu.upc.parserutils.FoldersUrl;
import edu.upc.parserutils.dm.DmnFreelingUtils;

public class App {

	static DmParser parser;
	private static File textFolder = new File(FoldersUrl.INPUT_FOLDER + "/texts");

	public static void main(String[] args) throws ParseException, IOException {
		if (args.length == 0) {
			for (File path : textFolder.listFiles()) {
				String nlpTextFilePath = path.toString();
				String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
				String extensionOfFile = FilenameUtils.getExtension(nlpTextFilePath);
				if (extensionOfFile.equals("txt")) {
					System.out.println("-------------------------------------------------");
					System.out.println("------ " + justNameOfFile + " -----");
					System.out.println("-------------------------------------------------");
					parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", "en");
					Drd drd = new Drd(parser.getDecisionList(), parser.getDecisionTableList(),
							parser.getRequirementList(), parser.getInputDataList());
					String xmlString = drd.generateDrd();

					Files.write(Paths.get(FoldersUrl.OUTPUT_FOLDER + "/dmn/" + justNameOfFile + ".dmn"),
							xmlString.getBytes());
				}
			}
		} else {
			String nlpTextFilePath = FoldersUrl.INPUT_FOLDER + "/texts/test.txt";
			String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
			parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", "en");
			Drd drd = new Drd(parser.getDecisionList(), parser.getDecisionTableList(), parser.getRequirementList(),
					parser.getInputDataList());
			String xmlString = drd.generateDrd();

			Files.write(Paths.get(FoldersUrl.OUTPUT_FOLDER + "/dmn/" + justNameOfFile + ".dmn"),
					xmlString.getBytes());
			System.out.println(xmlString);
		}
		System.out.println("DMN generator done!");
	}

}
