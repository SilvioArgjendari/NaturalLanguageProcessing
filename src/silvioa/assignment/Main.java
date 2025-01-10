package silvioa.assignment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  public static void main(String[] args) {
    String path = (args.length == 0) ? "languages" : args[0];
    start(path);

  }

  private static void start(String absolutePath) {
//    String absolutePath = (args.length == 0) ? "languages" : args[0];
    List<Path> folderList = listOfFolders(absolutePath);
    int n = userInput();

    // Read mystery file and construct mysteryHistogram
    List<String> mysteryFile = readFile(Paths.get(absolutePath + "\\mystery.txt"));
    LanguageModel mysteryModel = constructHistogram(mysteryFile, n, "Mystery");

    // Construct language models
    ConcurrentMap<String, LanguageModel> models =
        folderList.parallelStream()
            .map(folder -> defineModel(folder, n))
            .collect(Collectors.toConcurrentMap(LanguageModel::getLanguage, model -> model));

    ConcurrentMap<String, Double> similarities = getSimilarities(models, mysteryModel);

    printAllSimilarities(similarities);
    mostSimilar(similarities);
  }

  private static int userInput() {
    Scanner scanner = new Scanner(System.in);
    int n = 2;

    System.out.print("Enter n: ");
    String s = scanner.nextLine();

    if (!s.equals("")) {
      try {
        n = Integer.parseInt(s);
        if (n <= 0) n = 2;
      } catch (NumberFormatException e) {
        System.out.println("Not a valid number! N is now 2.");
      }
    }

    return n;
  }

  private static List<Path> listOfFolders(String absolutePath) {
    try {
      return Files.list(Paths.get(absolutePath)).filter(Files::isDirectory).collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalArgumentException(absolutePath + " is not a valid folder.", e);
    }
  }

  private static List<Path> listOfFiles(Path folderPath) {
    try {
      return Files.list(folderPath)
          .filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().endsWith(".txt"))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot read files from " + folderPath, e);
    }
  }

  private static List<String> readFile(Path path) {
    try {
      return Files.lines(path).
          collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Cannot read lines from " + path, e);
    }
  }

  private static List<String> concatFiles(Path folderPath) {
    return listOfFiles(folderPath).parallelStream()
        .map(Main::readFile)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private static String getLanguageName(Path folderPath) {
    return listOfFiles(folderPath).get(0).getFileName().toString().split("-")[0];
  }

  private static LanguageModel defineModel(Path folderPath, int n) {
    List<String> lines = concatFiles(folderPath);
    String language = getLanguageName(folderPath);
    return constructHistogram(lines, n, language);
  }

  private static LanguageModel constructHistogram(List<String> lines, int n, String language) {
    Map<String, Long> histogram =
        lines.stream()
            .map(line -> line.replaceAll("\\p{P}", ""))
            .map(String::toLowerCase)
            .flatMap(input -> Pattern.compile("\\s+").splitAsStream(input))
            .filter(word -> word.chars().noneMatch(Character::isDigit))
            .map(word -> nGramTokenizer(word, n))
            .flatMap(List::stream)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    return new LanguageModel(histogram, language);
  }

  private static List<String> nGramTokenizer(String word, int n) {
    if (word.length() < n)
      return IntStream.range(0, 1).mapToObj(i -> word).collect(Collectors.toList());
    return IntStream
        .range(0, word.length() - n + 1)
        .mapToObj(i -> word.substring(i, i + n))
        .collect(Collectors.toList());
  }

  private static ConcurrentMap<String, Double> getSimilarities(ConcurrentMap<String, LanguageModel> models, LanguageModel mystery) {
    return models.entrySet().parallelStream()
        .collect(Collectors.toConcurrentMap(Map.Entry::getKey,
            entry -> entry.getValue().calculateSimilarity(mystery)));
  }

  private static void printAllSimilarities(ConcurrentMap<String, Double> map) {
    map.entrySet()
        .parallelStream()
        .forEach(sim ->
            System.out.println("Similarity of mystery to " + sim.getKey() + ": " + sim.getValue()));
  }

  private static void mostSimilar(ConcurrentMap<String, Double> map) {
    String key = map.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    double value = map.entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue();

    System.out.println("Most similar language to Mystery text is " + key + " with a similarity of " + value);
  }

}
