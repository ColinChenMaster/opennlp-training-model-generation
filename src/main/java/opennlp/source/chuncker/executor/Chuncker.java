package opennlp.source.chuncker.executor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.util.Config;
import core.util.ReadTxtFile;
import opennlp.source.pos.executor.PosExecutor;
import opennlp.source.pos.executor.ResponseObject;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;

/**
 * @author Dhanushanth Here the tokenized words and tags need to be pass in to
 *         chunker as a input. Then the chunker able to generate the phrases
 *         according to the pattern.
 */
public class Chuncker {
	private static final Logger LOG = LoggerFactory.getLogger(Chuncker.class);
	private static InputStream modelInChunker = null;
	private static ChunkerModel chunkerModel;

	static {

		try {
			modelInChunker = new FileInputStream(Config.getModelDataPath() + "en-chunker.bin");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			chunkerModel = new ChunkerModel(modelInChunker);
			LOG.info("Chunker model has been loaded from " + Config.getModelDataPath() + "en-chunker.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, String> getPhrases() throws IOException {
//		String input = "I like to learn natural language processing and artificial intellegence";
		String input = ReadTxtFile.getString("build-training-models/paragraph.txt");

		ChunkerME chunkerME = new ChunkerME(chunkerModel);

		ResponseObject response = PosExecutor.getPOSTags(input);

		Span[] span = chunkerME.chunkAsSpans(response.getTokens(), response.getTags());
		Map<String, String> listOfWords = new HashMap<String, String>();
		for (Span s : span) {
			StringBuilder string = new StringBuilder();
			for (int i = s.getStart(); i < s.getEnd(); i++) {
				string.append((response.getTokens()[i]) + " ");
			}

			listOfWords.put(string.toString(), null);
		}
		return listOfWords;
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> words = getPhrases();
		System.out.println("\nList of Phrases : ");
		for (String string : words.keySet()) {
			System.out.println(string);
		}
	}

}
