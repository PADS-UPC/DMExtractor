package edu.upc.handler;

import java.util.ArrayList;
import java.util.Arrays;

import edu.upc.parserutils.Language;

public class LangHandler {
	static private final ArrayList<String> LANGS = new ArrayList<>(Arrays.asList("en", "es"));

	static public void changeFoldersPath(String lang) {
		lang = lang.toLowerCase();
		if (LANGS.contains(lang)) {
			Language.LANG.setDescription(lang);
			System.out.println("--> Language default: EN");
			// System.out.println("--> Language changed to: " + lang.toUpperCase() + "-->
			// Freeling4bpm");
		} else {
			System.out.println("The system does not support \"" + lang + "\" language yet");
			System.out.println("The system is taking the English language by default...");
		}
	}
}
