package refactor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LanguageModel {
  private final Histogram histogram;
  private final int nGramSize;

  public LanguageModel(int nGramSize) {
    this.nGramSize = nGramSize;
    this.histogram = new Histogram();
  }

  public Histogram getHistogram() {
    return histogram;
  }

  public void updateFrequencies(String token) {
    histogram.increment(token);
  }

  public void processText(String text) {
    List<String> tokenizedText = generateNGrams(text);
    tokenizedText.forEach(this::updateFrequencies);
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

//  @Deprecated
//  public double calculateSimilarity(LanguageModel other) {
//    double dotProduct = histogram.calculateDotProduct(other.getFrequencies());
//    return (dotProduct) / (histogram.calculateNorm() * other.calculateNorm());
//  }
}
