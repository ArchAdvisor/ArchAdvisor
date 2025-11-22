package at.ac.ubik.archadvisor.domain;

import java.util.List;

public record RecommendationResult(
        List<Recommendation> backends,
        List<Recommendation> frontends,
        List<Recommendation> databases,
        List<Recommendation> mobileFrameworks
) {

    public static RecommendationResult backendOnly(List<Recommendation> backends) {
        return new RecommendationResult(backends, List.of(), List.of(), List.of());
    }

    public static RecommendationResult fullStack(
            List<Recommendation> backends,
            List<Recommendation> frontends,
            List<Recommendation> databases
    ) {
        return new RecommendationResult(backends, frontends, databases, List.of());
    }

    public static RecommendationResult mobileOnly(List<Recommendation> mobile) {
        return new RecommendationResult(List.of(), List.of(), List.of(), mobile);
    }
}

