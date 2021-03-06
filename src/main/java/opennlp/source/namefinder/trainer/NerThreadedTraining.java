package opennlp.source.namefinder.trainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import opennlp.source.sentencer.executor.SentenceDetector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.util.Config;
import core.util.FileUtils;
import core.util.ReadTxtFile;
import core.util.WriteFile;

class ExecureThread implements Runnable {
	private List<Path> filePaths;
	private int id;

	ExecureThread(List<Path> filePaths, int id) {
		this.filePaths = filePaths;
		this.id = id;
	}

	public void run() {
		for (Path path : filePaths) {
			String text = ReadTxtFile.getXmlExtString(path.toString());
			String[] sentences = SentenceDetector.getSentences(text);
			for (String sentence : sentences) {
				String result = CreateTrainingData.getOpenNLPTaggedText(sentence, Config.getNERTrainingEntity());
				WriteFile.writeDataWithoutOverwrite(Config.getTrainDataPath() + "en-ner-person-" + id + ".train", result);
			}
		}
	}
}

public class NerThreadedTraining {
	static int number_of_threads = Config.getNumberOfThread();
	private static final Logger LOG = LoggerFactory.getLogger(NerThreadedTraining.class);

	public static void main(String args[]) {
		LOG.info("Started threaded train data creation for ner process.");
		FileUtils.CreateMultiDirec(Config.getTrainDataPath());

		ArrayList<Path> listOfFiles = new ArrayList<>();
		try {
			Files.walk(Paths.get(Config.getTextSourcePath())).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					listOfFiles.add(filePath);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOG.info("Total number of files : " + listOfFiles.size());

		int number_of_divide = listOfFiles.size() / number_of_threads;
		int balance = listOfFiles.size() % number_of_threads;

		LOG.info("Starting threading process");
		for (int i = 0; i <= number_of_threads; i++) {
			if (i == number_of_threads) {
				LOG.info("selected files : " + (i * number_of_divide) + " " + (i * number_of_divide + balance));
				ExecureThread executor = new ExecureThread(listOfFiles.subList((i * number_of_divide), (i * number_of_divide + balance)), i);
				Thread thread = new Thread(executor);
				thread.start();
			} else {
				LOG.info("selected files : " + (i * number_of_divide) + " " + (i * number_of_divide + number_of_divide));
				ExecureThread executor = new ExecureThread(listOfFiles.subList((i * number_of_divide), (i * number_of_divide + number_of_divide)), i);
				Thread thread = new Thread(executor);
				thread.start();
			}

		}

		LOG.info("thread calls for training data extraction done.");
	}
}