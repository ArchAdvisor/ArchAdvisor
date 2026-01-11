package at.ac.ubik.archadvisor.domain;

import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;

import java.util.List;

public record RecommendationResult(
        ArchitectureScope architectureScope,
        List<Recommendation> backends,
        List<Recommendation> frontends,
        List<Recommendation> databases,
        List<Recommendation> mobileFrameworks
) {

    public static RecommendationResult backendOnly(ArchitectureScope architectureScope, List<Recommendation> backends) {
        return new RecommendationResult(architectureScope, backends, List.of(), List.of(), List.of());
    }

    public static RecommendationResult fullStack(
            ArchitectureScope architectureScope,
            List<Recommendation> backends,
            List<Recommendation> frontends,
            List<Recommendation> databases
    ) {
        return new RecommendationResult(architectureScope, backends, frontends, databases, List.of());
    }

    public static RecommendationResult mobileOnly(ArchitectureScope architectureScope, List<Recommendation> mobile) {
        return new RecommendationResult(architectureScope, List.of(), List.of(), List.of(), mobile);
    }
}

