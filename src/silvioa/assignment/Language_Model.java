package silvioa.assignment;

import java.util.Map;

public class Language_Model {
    private final Map<String, Long> histogram;
    private final String language;

    public Language_Model(Map<String, Long> histogram, String language) {
        this.histogram = histogram;
        this.language = language;
    }

    public Map<String, Long> getHistogram() {
        return histogram;
    }

    public String getLanguage() {
        return language;
    }

    public double norm() {
        return Math.sqrt(getHistogram().values().stream()
                .mapToLong(v -> v * v)
                .sum());
    }

    public long dotProduct (Language_Model mystery) {
        return this.getHistogram().entrySet()
                .stream()
                .filter(entry -> mystery.getHistogram().containsKey(entry.getKey()))
                .map(entry -> entry.getValue() * mystery.getHistogram().get(entry.getKey()))
                .reduce(0L, (x, y) -> x + y);
    }

    public double calculateSimilarity (Language_Model mystery) {
        return this.dotProduct(mystery) / (this.norm() * mystery.norm());
    }

    public double calculateAngle (Language_Model mystery) {
        double cosineSimilarity = this.calculateSimilarity(mystery);
        return Math.acos(cosineSimilarity) * 180 / Math.PI;     // In degrees
    }
}
