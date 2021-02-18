package edu.upc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;

import edu.upc.freelingutils.dm.DmnFoldersUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;

public class DrdGenerator {

	static String lang = "en";
	static DmParser parser;
	private static File textFolder = new File(
			System.getProperty("user.home") + "/doc2dmnutils/" + lang + "/input/texts");

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
					parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", lang);
					Drd drd = new Drd(parser.getDecisionList(), parser.getDecisionTableList(),
							parser.getRequirementList(), parser.getInputDataList());
					String xmlString = drd.generateDrd();

					Files.write(Paths.get(DmnFoldersUrl.OUTPUT_FOLDER + "/dmn/" + justNameOfFile + ".dmn"),
							xmlString.getBytes());
				}
			}
		} else {
			String nlpTextFilePath = System.getProperty("user.home") + "/doc2dmnutils/" + lang
					+ "/input/texts/test.txt";
			String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
			parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", lang);
			///////////////
			Drd drd = new Drd(parser.getDecisionList(), parser.getDecisionTableList(), parser.getRequirementList(),
					parser.getInputDataList());
			String xmlString = drd.generateDrd();

			Files.write(Paths.get(DmnFoldersUrl.OUTPUT_FOLDER + "/dmn/" + justNameOfFile + ".dmn"),
					xmlString.getBytes());
			System.out.println(xmlString);
		}
		System.out.println("DMN generator done!");
	}

}
