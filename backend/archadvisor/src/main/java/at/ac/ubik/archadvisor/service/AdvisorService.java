package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.domain.*;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.domain.enums.LicenseType;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.TechnologyRepository;
import at.ac.ubik.archadvisor.mapper.TechnologyMapper;
import at.ac.ubik.archadvisor.recommendation.ISuggestionAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
public class AdvisorService {


    private final TechnologyRepository technologyRepository;
    private final ISuggestionAlgorithm algorithm;
    private final TechnologyMapper mapper;

    public AdvisorService(TechnologyRepository technologyRepository, ISuggestionAlgorithm algorithm, TechnologyMapper technologyMapper) {
        this.technologyRepository = technologyRepository;
        this.algorithm = algorithm;
        this.mapper = technologyMapper;
    }

    private List<Technology> loadCatalog() {
        return technologyRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }


    public RecommendationResult suggest(RecommendationContext ctx, long numberOfCandidates) {
        ArchitectureScope scope = ctx.getTechnicalProfile().getScope();
        List<Recommendation> backendRecommendations;
        List<Recommendation> frontRecommendations;
        List<Recommendation> databaseRecommendations;
        List<Recommendation> mobileFrameworkRecommendations;
        switch (scope) {
            case BACKEND_ONLY:
                backendRecommendations = getTopNRecommendations(
                        BackendFramework.class,
                        ctx,
                        numberOfCandidates,
                        (tech) -> filterByTechnicalProfile(tech, ctx));
                return RecommendationResult.backendOnly(scope, backendRecommendations);
            case FULL_STACK:
                backendRecommendations = getTopNRecommendations(
                        BackendFramework.class,
                        ctx,
                        numberOfCandidates,
                        (tech) -> filterByTechnicalProfile(tech, ctx));
                frontRecommendations = getTopNRecommendations(
                        FrontendFramework.class,
                        ctx,
                        numberOfCandidates,
                        (tech) -> filterByTechnicalProfile(tech, ctx));
                databaseRecommendations = getTopNRecommendations(
                        DatabaseTech.class,
                        ctx,
                        numberOfCandidates, (tech) -> filterByTechnicalProfile(tech, ctx));

                return RecommendationResult.fullStack(scope, backendRecommendations, frontRecommendations, databaseRecommendations);
            case MOBILE:
                mobileFrameworkRecommendations = getTopNRecommendations(
                        MobileFramework.class,
                        ctx,
                        numberOfCandidates, (tech) -> filterByTechnicalProfile(tech, ctx));
                return RecommendationResult.mobileOnly(scope, mobileFrameworkRecommendations);
            default:
                throw new IllegalArgumentException("Unknown scope");
        }
    }

    private <T extends Technology> List<Recommendation> getTopNRecommendations(
            Class<T> type,
            RecommendationContext ctx,
            long n,
            Predicate<? super T> extraFilter
    ) {
        Predicate<? super T> effectiveFilter =
                extraFilter != null ? extraFilter : t -> true;
        List<Technology> catalog = loadCatalog();
        return catalog.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(effectiveFilter)
                .map(t -> new Recommendation(t, algorithm.getScore(t, ctx), List.of()))
                .sorted(Comparator.comparingDouble(Recommendation::getScore).reversed())
                .limit(n)
                .toList();
    }

    public boolean filterByTechnicalProfile(Technology tech, RecommendationContext ctx) {
        TechnicalProfile technicalProfile = ctx.getTechnicalProfile();

        if (technicalProfile.isWantsOpenSourceOnly() &&
                tech.getLicense() == LicenseType.PROPRIETARY) {
            return false;
        }

        // TODO: add more filters: architecture scope, deployment, etc.
        return true;
    }
}

