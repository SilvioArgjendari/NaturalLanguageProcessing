package refactor;

import refactor.concurrent.ConcurrentProcessor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LanguageClassifier {
  private final TextProcessor textprocessor;
  private final ConcurrentProcessor concurrentProcessor;
  private final int nGramSize;

  public LanguageClassifier(int nGramSize) {
    this.textprocessor = new TextProcessor();
    this.concurrentProcessor = new ConcurrentProcessor(textprocessor);
    this.nGramSize = nGramSize;
  }

  private String processMysteryText(String folderPath) {
    Path mysterypath = Path.of(folderPath, "mystery.txt");
    List<String> mysteryLines = textprocessor.readFile(mysterypath);
    String mysteryText = String.join(" ", mysteryLines);
    return textprocessor.preprocessText(mysteryText);
  }

  public void classifyText(String folderPath) {
    Map<String, LanguageModel> languageModels = buildLanguageModels(folderPath);

    String mysteryText = processMysteryText(folderPath);
    LanguageModel mysteryModel = new LanguageModel(nGramSize);
    mysteryModel.processText(mysteryText);

    Map<String, Double> similarityMap = languageModels.entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            model -> VectorMath.calculateSimilarity(model.getValue().getHistogram(), mysteryModel.getHistogram())
        ));

    similarityMap.forEach((language, similarity) -> System.out.println(language + ": " + similarity));

    similarityMap.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .ifPresent(entry -> System.out.println("Detected language: " + entry.getKey()));

  }

  public Map<String, LanguageModel> buildLanguageModels(String folderPath) {
    Path rootPath = Path.of(folderPath);
    List<Path> languageFolders = textprocessor.getLanguageFolders(rootPath);

    return languageFolders.stream()
        .collect(Collectors.toMap(
            folder -> folder.getFileName().getFileName().toString(),
            folder -> {
              LanguageModel model = new LanguageModel(nGramSize);
              concurrentProcessor.processLanguageFolder(folder, model);
              return model;
            }
        ));
  }

  public void findMostSimilarLanguage(String mysteryText, Map<String, LanguageModel> models) {
    LanguageModel mysteryModel = new LanguageModel(nGramSize);
    mysteryModel.processText(mysteryText);

//    return models.entrySet().stream()
//        .map(entry -> Map.entry(
//            entry.getKey(),
//            entry.getValue().calculateSimilarity(mysteryModel)
//        ))
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
//    nGramSize = scanner.nextInt();

    int nGramSize = 2;
    LanguageClassifier classifier = new LanguageClassifier(nGramSize);
    classifier.classifyText("languages");

  }


}
