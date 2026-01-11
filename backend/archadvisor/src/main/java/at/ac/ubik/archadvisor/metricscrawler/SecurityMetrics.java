package at.ac.ubik.archadvisor.metricscrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to load security scores (e.g. based on CVEs) from a CSV file.
 * CSV format (example):
 *
 * technology,security_score
 * spring-boot,0.9
 * express,0.6
 */
public class SecurityMetrics {

    /**
     * Loads security scores from a CSV file.
     * @param csvPath path to the CSV file (e.g. "security_scores.csv")
     * @return Map: technology name -> security score (0..1)
     */
    public static Map<String, Double> loadSecurityScores(String csvPath) {
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
            System.err.println("Error loading security scores from " + csvPath + ": " + e.getMessage());
        }

        return result;
    }

    /**
     * Convenience method to get the security score for a single technology.
     */
    public static double getSecurityFor(Map<String, Double> secMap, String technology) {
        return secMap.getOrDefault(technology, 0.0);
    }

    /**
     * Small main method to test this class independently.
     */
    public static void main(String[] args) {
        String csvPath = "src/main/resources/arch-metric/security_scores.csv";

        Map<String, Double> scores = loadSecurityScores(csvPath);

        System.out.println("All security scores:");
        System.out.println(scores);

        String tech = "spring-boot"; // adjust to a technology that exists in your CSV
        double value = getSecurityFor(scores, tech);
        System.out.println("Security score for '" + tech + "': " + value);
    }
}
