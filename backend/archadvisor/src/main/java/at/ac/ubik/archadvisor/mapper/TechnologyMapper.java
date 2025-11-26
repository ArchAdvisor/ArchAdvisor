package at.ac.ubik.archadvisor.mapper;

import at.ac.ubik.archadvisor.domain.*;
import at.ac.ubik.archadvisor.domain.enums.PriorityAspect;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TechnologyMapper {

    public Technology toDomain(TechnologyEntity e) {

        Technology technology;
        switch (e.getKind()) {
            case BACKEND -> technology = new BackendFramework(
                    e.getId(),
                    e.getName(),
                    e.getDescription(),
                    e.getLicense(),
                    parseTags(e.getTagsCsv()),
                    e.getGithubUrl(),
                    e.getDocumentationUrl(),
                    e.getLastUpdated(),
                    e.getLanguage(),
                    e.getRuntime(),
                    e.isServerlessFriendly()
            );
            case FRONTEND -> technology = new FrontendFramework(
                    e.getId(),
                    e.getName(),
                    e.getDescription(),
                    e.getLicense(),
                    parseTags(e.getTagsCsv()),
                    e.getGithubUrl(),
                    e.getDocumentationUrl(),
                    e.getLastUpdated(),
                    e.getLanguage(),
                    e.getRuntime(),
                    e.isSupportsSSR()
            );
            case DATABASE -> technology = new DatabaseTech(
                    e.getId(),
                    e.getName(),
                    e.getDescription(),
                    e.getLicense(),
                    parseTags(e.getTagsCsv()),
                    e.getGithubUrl(),
                    e.getDocumentationUrl(),
                    e.getLastUpdated(),
                    e.getDbModelType()
            );
            case MOBILE -> technology = new MobileFramework(
                    e.getId(),
                    e.getName(),
                    e.getDescription(),
                    e.getLicense(),
                    parseTags(e.getTagsCsv()),
                    e.getGithubUrl(),
                    e.getDocumentationUrl(),
                    e.getLastUpdated(),
                    e.getMobilePlatform()
            );
            default -> throw new IllegalArgumentException("Unknown technology type");
        }
        technology.setValueOfMetricScore(PriorityAspect.PERFORMANCE, e.getPerformanceScore());
        technology.setValueOfMetricScore(PriorityAspect.SCALABILITY, e.getScalabilityScore());
        technology.setValueOfMetricScore(PriorityAspect.MAINTAINABILITY, e.getMaintainabilityScore());
        technology.setValueOfMetricScore(PriorityAspect.SECURITY, e.getSecurityScore());
        technology.setValueOfMetricScore(PriorityAspect.COST_EFFECTIVENESS, e.getCostEffectivenessScore());
        technology.setValueOfMetricScore(PriorityAspect.COMMUNITY_SUPPORT, e.getCommunitySupportScore());
        technology.setValueOfMetricScore(PriorityAspect.ECOSYSTEM_MATURITY, e.getEcosystemMaturityScore());
        technology.setValueOfMetricScore(PriorityAspect.VENDOR_LOCKIN_AVOIDANCE, e.getVendorLockinScore());
        return technology;
    }

    private Set<String> parseTags(String csv) {
        if (csv == null || csv.isEmpty()) return Set.of();
        return Set.of(csv.split(","));
    }
}

