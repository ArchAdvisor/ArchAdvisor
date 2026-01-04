package at.ac.ubik.archadvisor.metricscrawler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GithubStarsMetrics {

    /**
     * Reads a CSV file like "github_stars.csv" and returns
     * a Map<String, Double> with technology -> stars (or normalised score).
     */
    public static Map<String, Double> loadGithubStars(String csvPath) {
        Map<String, Double> stars = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // skip header
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

                stars.put(technology, value);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading GitHub stars from " + csvPath + ": " + e.getMessage());
        }

        return stars;
    }

    /**
     * Convenience method to get the value for a single technology.
     */
    public static double getStarsFor(Map<String, Double> starsMap, String technology) {
        return starsMap.getOrDefault(technology, 0.0);
    }

    public static void main(String[] args) {
        // 1) load all values from the CSV
        String csvPath = "src/main/resources/arch-metric/github_stars.csv";
        Map<String, Double> stars = loadGithubStars(csvPath);

        // 2) print everything
        System.out.println("All GitHub stars:");
        System.out.println(stars);

        // 3) example: show value for one framework
        String tech = "react"; 
        double value = getStarsFor(stars, tech);
        System.out.println("GitHub stars metric for '" + tech + "': " + value);
    }
}




