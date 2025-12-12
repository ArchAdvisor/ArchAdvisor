package at.ac.ubik.archadvisor.domain;


import at.ac.ubik.archadvisor.domain.enums.LicenseType;
import at.ac.ubik.archadvisor.domain.enums.PriorityAspect;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public abstract class Technology {

    private final Long id;
    private final String name;
    private final String description;
    private final LicenseType license;
    private final Set<String> tags;
    private final String githubUrl;
    private final String documentationUrl;
    private final Instant lastUpdated;

    private final Map<PriorityAspect, Double> metricScores = new EnumMap<>(PriorityAspect.class);

    protected Technology(Long id,
                         String name,
                         String description,
                         LicenseType license,
                         Set<String> tags,
                         String githubUrl,
                         String documentationUrl,
                         Instant lastUpdated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.license = license;
        this.tags = tags;
        this.githubUrl = githubUrl;
        this.documentationUrl = documentationUrl;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LicenseType getLicense() {
        return license;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setMetricScore(PriorityAspect aspect, double value) {
        metricScores.put(aspect, value);
    }

    public double getMetricScore(PriorityAspect aspect) {
        return metricScores.get(aspect);
    }

    public void setValueOfMetricScore(PriorityAspect aspect, double value) {
        metricScores.put(aspect, value);
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }
}
