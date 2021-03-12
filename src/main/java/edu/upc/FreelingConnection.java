package edu.upc;

import java.io.IOException;
import clojure.java.api.Clojure;
import clojure.lang.ILookup;
import clojure.lang.Keyword;
import edu.upc.nlp4bpm_commons.Cache;
import edu.upc.nlp4bpm_commons.FreelingAPI;
import edu.upc.parserutils.FilesUrl;
import edu.upc.parserutils.Language;
import edu.upc.parserutils.dm.DmnFilesUrl;
import edu.upc.parserutils.dm.DmnFreelingUtils;

public class FreelingConnection {

	private String cacheUrl = FilesUrl.FREELING_CACHE_FILE.toString();

	public String getJsonString(String text) throws IOException {
		System.out.println("Connecting to Freeling...");
		String credentials = DmnFreelingUtils.readFile(DmnFilesUrl.TEXTSERVER_CREDENTIALS.toString());
		ILookup obj = (ILookup) Clojure.read(credentials);
		Cache.initialize(cacheUrl);
		// FreelingAPI.setMode("local");
		String jsonOut = FreelingAPI.analyzeCachedWithUri(text, Language.LANG.toString(), "json", "semgraph",
				obj.valAt(Keyword.intern("username")).toString(), obj.valAt(Keyword.intern("password")).toString(),
				obj.valAt(Keyword.intern("uri")).toString(),
				Boolean.parseBoolean(obj.valAt(Keyword.intern("use-json")).toString()));
		Cache.saveCache();
		return jsonOut;
	}

}