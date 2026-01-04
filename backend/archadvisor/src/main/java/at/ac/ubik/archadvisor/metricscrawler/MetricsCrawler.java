package at.ac.ubik.archadvisor.metricscrawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * MetricsCrawler
 * -------------
 * This class combines the three metric sources:
 *  - GitHub stars
 *  - Performance scores
 *  - Security scores
 *
 * It provides a single method to load all metrics for all technologies.
 */
public class MetricsCrawler {

    // Paths to the CSV files (same folder you created)
    private static final String GITHUB_CSV      = "src/main/resources/arch-metric/github_stars.csv";
    private static final String PERFORMANCE_CSV = "src/main/resources/arch-metric/performance_scores.csv";
    private static final String SECURITY_CSV    = "src/main/resources/arch-metric/security_scores.csv";

    /**
     * Simple DTO to hold all metrics for one technology.
     */
    public static class TechnologyMetrics {
        public double githubStars;
        public double performanceScore;
        public double securityScore;

        @Override
        public String toString() {
            return "TechnologyMetrics{" +
                    "githubStars=" + githubStars +
                    ", performanceScore=" + performanceScore +
                    ", securityScore=" + securityScore +
                    '}';
        }
    }

    /**
     * Load all metrics from CSVs and merge them into a single Map.
     *
     * key   = technology name (e.g. "react")
     * value = TechnologyMetrics with all three scores
     */
    public static Map<String, TechnologyMetrics> loadAllMetrics() {
        // 1) Load each metric into its own map
        Map<String, Double> githubStarsMap =
                GithubStarsMetrics.loadGithubStars(GITHUB_CSV);

        Map<String, Double> performanceMap =
                PerformanceMetrics.loadPerformanceScores(PERFORMANCE_CSV);

        Map<String, Double> securityMap =
                SecurityMetrics.loadSecurityScores(SECURITY_CSV);

        // 2) Build the union of all technology names
        Set<String> allTechnologies = new HashSet<>();
        allTechnologies.addAll(githubStarsMap.keySet());
        allTechnologies.addAll(performanceMap.keySet());
        allTechnologies.addAll(securityMap.keySet());

        // 3) Create a combined map
        Map<String, TechnologyMetrics> result = new HashMap<>();

        for (String tech : allTechnologies) {
            TechnologyMetrics tm = new TechnologyMetrics();
            tm.githubStars = githubStarsMap.getOrDefault(tech, 0.0);
            tm.performanceScore = performanceMap.getOrDefault(tech, 0.0);
            tm.securityScore = securityMap.getOrDefault(tech, 0.0);
            result.put(tech, tm);
        }

        return result;
    }

    /**
     * Convenience method: get metrics for a single technology.
     */
    public static TechnologyMetrics getMetricsFor(String technology) {
        Map<String, TechnologyMetrics> all = loadAllMetrics();
        return all.get(technology); // may be null if tech not found
    }

    /**
     * Small demo main – for local testing only.
     */
    public static void main(String[] args) {
        Map<String, TechnologyMetrics> all = loadAllMetrics();

        System.out.println("=== All technologies with metrics ===");
        for (Map.Entry<String, TechnologyMetrics> entry : all.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println();
        String exampleTech = "react";
        TechnologyMetrics metricsForReact = getMetricsFor(exampleTech);
        System.out.println("Metrics for '" + exampleTech + "': " + metricsForReact);
    }
}

