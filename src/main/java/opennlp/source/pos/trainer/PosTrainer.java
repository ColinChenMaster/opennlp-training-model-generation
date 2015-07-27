package opennlp.source.pos.trainer;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import core.util.Config;
import core.util.ReadTxtFile;
import core.util.WriteFile;
import opennlp.source.sentencer.SentenceDetector;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * Create part-of-speech tags using opennlp
 * 
 * @author root
 *
 */
public class PosTrainer {

	public static void generatePosTags(String text) throws IOException {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		String[] sentencess = SentenceDetector.getSentences(text);
		for (String string : sentencess) {
			Annotation document = new Annotation(string);

			pipeline.annotate(document);

			List<CoreMap> sentences = document.get(SentencesAnnotation.class);

			StringBuffer sb = new StringBuffer();
			for (CoreMap sentence : sentences) {
				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					String word = token.get(TextAnnotation.class);
					String pos = token.get(PartOfSpeechAnnotation.class);
					sb.append(word + "_" + pos + " ");
				}
			}
			String result = sb.toString().replaceAll("``", "“").replaceAll("''", "”").trim();
			System.out.println(result);
			WriteFile.writeDataWithoutOverwrite(Config.getTrainDataPath() + "en-pos.train", result);
		}

	}

	public static void main(String[] args) throws IOException {
//		String text = "“James B Stewart“ Common Sense column observes Apple 75%, formerly market laggard, has far distanced Microsoft in share price since January 2014.";
		String text = ReadTxtFile.getString("build-training-models/paragraph.txt");
		generatePosTags(text);
	}
}