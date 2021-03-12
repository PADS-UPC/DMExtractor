package edu.upc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;
import edu.stanford.nlp.trees.Tree;
import edu.upc.parserutils.FoldersUrl;
import edu.upc.parserutils.dm.DmnFreelingUtils;

public class DmTester {

	private static File textFolder = new File(FoldersUrl.INPUT_FOLDER + "/texts");
	private static File treeFolder;

	public static void main(String[] args) throws ParseException, IOException {
		treeFolder = new File(FoldersUrl.TREE_FOLDER.toString());
		if (args.length == 0) {
			for (File path : textFolder.listFiles()) {
				String nlpTextFilePath = path.toString();
				String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
				String extensionOfFile = FilenameUtils.getExtension(nlpTextFilePath);
				if (extensionOfFile.equals("txt")) {
					DmParser parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", "en");
					Files.write(Paths.get(treeFolder + "/" + justNameOfFile + ".trx"),
							parser.getTrees().toString().getBytes());
					System.out.println("> Tree: " + treeFolder + "/" + justNameOfFile + ".trx");
					Tree superTree = null;
					superTree = DmnFreelingUtils.makeSuperTree(parser.getTrees());
					Files.write(Paths.get(FoldersUrl.OUTPUT_FOLDER + "/tree/super_" + justNameOfFile + ".trx"),
							superTree.toString().getBytes());
				}
			}
		} else {
			String nlpTextFilePath = FoldersUrl.INPUT_FOLDER + "/texts/test.txt";
			String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
			DmParser parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", "en");
			Files.write(Paths.get(treeFolder + "/" + justNameOfFile + ".trx"), parser.getTrees().toString().getBytes());
			System.out.println("> Tree: " + treeFolder + "/" + justNameOfFile + ".trx");
			Tree superTree = null;
			superTree = DmnFreelingUtils.makeSuperTree(parser.getTrees());
			Files.write(Paths.get(FoldersUrl.OUTPUT_FOLDER + "/tree/super_" + justNameOfFile + ".trx"),
					superTree.toString().getBytes());

		}
		System.out.println("DMExtractor DONE!");
	}
}
