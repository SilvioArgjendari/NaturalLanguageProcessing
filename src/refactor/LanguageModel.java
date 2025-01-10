package refactor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LanguageModel {
  private Histogram histogram;
  private int nGramSize;

  public LanguageModel(int nGramSize) {
    this.nGramSize = nGramSize;
    this.histogram = new Histogram();
  }

  public void updateFrequencies(String text) {
    histogram.increment(text);
  }

  // Maybe move to another class??
  public double calculateSimilarity(Histogram other) {
    double dotProduct = histogram.calculateDotProduct(other);
    return (dotProduct) / (histogram.calculateNorm() * other.calculateNorm());
  }

  public List<String> generateNGrams(String text) {
    String[] words = text.split("\\s+");

    return Stream.of(words)
        .flatMap(this::wordTokenizer)
        .collect(Collectors.toList());
  }

  private Stream<String> wordTokenizer(String word) {
    if (word.length() < nGramSize)
      return Stream.of(word);

    return IntStream.rangeClosed(0, word.length() - nGramSize)
        .mapToObj(i -> word.substring(i, i + nGramSize));
  }

  private List<String> wordTokenizer1(String word) {
    if (word.length() < nGramSize)
      return new ArrayList<>(List.of(word));
    return IntStream.rangeClosed(0, word.length() - nGramSize)
        .mapToObj(i -> word.substring(i, i + nGramSize))
        .collect(Collectors.toList());
  }


}
