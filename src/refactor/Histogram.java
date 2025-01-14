package refactor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Histogram {
  private final Map<String, Integer> frequencies;

  public Histogram() {
    this.frequencies = new ConcurrentHashMap<>();
  }

  @Deprecated
  public void merge(Histogram other) {
    other.frequencies.forEach((k, v) ->
        frequencies.merge(k, v, Integer::sum));
  }

  public void increment(String item) {
    frequencies.merge(item, 1, Integer::sum);
  }

  public int getFrequency(String item) {
    return frequencies.getOrDefault(item, 0);
  }

  public Map<String, Integer> getFrequencies() {
    return Collections.unmodifiableMap(frequencies);
  }

  public Set<String> getItems() {
    return frequencies.keySet();
  }

  // Move to Vector Math
  @Deprecated
  public double calculateDotProduct(Histogram other) {
    return frequencies.entrySet().stream()
        .mapToDouble(entry -> entry.getValue() * other.frequencies.getOrDefault(entry.getKey(), 0))
        .sum();
  }

  // Move to VectorMath
  @Deprecated
  public double calculateNorm() {
    return Math.sqrt(frequencies.values().stream()
        .mapToDouble(v -> v * v)
        .sum());
  }

}
