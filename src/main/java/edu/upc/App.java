package edu.upc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;
import edu.stanford.nlp.trees.Tree;
import edu.upc.freelingutils.dm.DmnFoldersUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;
import edu.upc.handler.LangHandler;

public class App {

	static String lang = "en";

	private static File textFolder = new File(
			System.getProperty("user.home") + "/doc2dmnutils/" + lang + "/input/texts");
	private static File treeFolder;

	public static void main(String[] args) throws ParseException, IOException {
		LangHandler.changeFoldersPath(lang);

		treeFolder = new File(DmnFoldersUrl.TREE_FOLDER.toString());
		if (args.length == 0) {
			for (File path : textFolder.listFiles()) {
				String nlpTextFilePath = path.toString();
				String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
				String extensionOfFile = FilenameUtils.getExtension(nlpTextFilePath);
				if (extensionOfFile.equals("txt")) {
					DmParser parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", lang);
					Files.write(Paths.get(treeFolder + "/" + justNameOfFile + ".trx"),
							parser.getTrees().toString().getBytes());
					System.out.println("> Tree: " + treeFolder + "/" + justNameOfFile + ".trx");
					Tree superTree = null;
					superTree = DmnFreelingUtils.makeSuperTree(parser.getTrees());
					Files.write(Paths.get(DmnFoldersUrl.OUTPUT_FOLDER + "/tree/super_" + justNameOfFile + ".trx"),
							superTree.toString().getBytes());
				}
			}
		} else {
			String nlpTextFilePath = DmnFoldersUrl.INPUT_FOLDER + "/texts/test.txt";
			String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
			DmParser parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", lang);
			Files.write(Paths.get(treeFolder + "/" + justNameOfFile + ".trx"), parser.getTrees().toString().getBytes());
			System.out.println("> Tree: " + treeFolder + "/" + justNameOfFile + ".trx");
			Tree superTree = null;
			superTree = DmnFreelingUtils.makeSuperTree(parser.getTrees());
			Files.write(Paths.get(DmnFoldersUrl.OUTPUT_FOLDER + "/tree/super_" + justNameOfFile + ".trx"),
					superTree.toString().getBytes());

		}
		System.out.println("DMExtractor DONE!");
	}
}
