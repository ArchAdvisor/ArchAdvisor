package at.ac.ubik.archadvisor.mapper;

import at.ac.ubik.archadvisor.domain.*;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TechnologyMapper {

    public Technology toDomain(TechnologyEntity e) {

        return switch (e.getKind()) {
            case BACKEND -> new BackendFramework(
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
            case FRONTEND -> new FrontendFramework(...);
            case DATABASE -> new DatabaseTech(...);
            case MOBILE -> new MobileFramework(...);
        };
    }

    private Set<String> parseTags(String csv) {
        if (csv == null || csv.isEmpty()) return Set.of();
        return Set.of(csv.split(","));
    }
}

