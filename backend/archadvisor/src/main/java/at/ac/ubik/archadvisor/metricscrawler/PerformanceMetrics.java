package at.ac.ubik.archadvisor.metricscrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to load performance scores from a CSV file.
 * CSV format (example):
 *
 * technology,performance
 * spring-boot,0.85
 * express,0.70
 */
public class PerformanceMetrics {

    /**
     * Loads performance scores from a CSV file.
     * @param csvPath path to the CSV file (e.g. "performance_scores.csv")
     * @return Map: technology name -> performance score (0..1)
     */
    public static Map<String, Double> loadPerformanceScores(String csvPath) {
        Map<String, Double> result = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue;
                }

                String technology = parts[0].trim();
                double value = Double.parseDouble(parts[1].trim());

                result.put(technology, value);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading performance scores from " + csvPath + ": " + e.getMessage());
        }

        return result;
    }

    /**
     * Convenience method to get the performance score for a single technology.
     */
    public static double getPerformanceFor(Map<String, Double> perfMap, String technology) {
        return perfMap.getOrDefault(technology, 0.0);
    }

    /**
     * Small main method to test this class independently.
     */
    public static void main(String[] args) {
        String csvPath = "src/main/resources/arch-metric/performance_scores.csv";
        Map<String, Double> scores = loadPerformanceScores(csvPath);

        System.out.println("All performance scores:");
        System.out.println(scores);

        String tech = "spring-boot"; // adjust to a technology that exists in your CSV
        double value = getPerformanceFor(scores, tech);
        System.out.println("Performance score for '" + tech + "': " + value);
    }
}
