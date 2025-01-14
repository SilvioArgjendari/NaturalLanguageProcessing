package refactor;

import java.util.Map;

public class VectorMath {
  public static double calculateDotProduct(Histogram v1, Histogram v2) {
    return v1.getFrequencies().entrySet().stream()
        .mapToDouble(entry -> entry.getValue() * v2.getFrequencies().getOrDefault(entry.getKey(), 0))
        .sum();
  }

  public static double calculateNorm(Histogram vector) {
    return Math.sqrt(vector.getFrequencies().values().stream()
        .mapToDouble(v -> v * v)
        .sum());
  }

  public static double calculateSimilarity(Histogram v1, Histogram v2) {
    double dotProduct = calculateDotProduct(v1, v2);
    double result = (dotProduct) / (calculateNorm(v1) * calculateNorm(v2));

    return Double.parseDouble(String.format("%.4f", result));
  }

}
