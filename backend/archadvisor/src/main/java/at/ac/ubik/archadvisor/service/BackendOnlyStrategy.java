package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.domain.RecommendationContext;
import at.ac.ubik.archadvisor.domain.RecommendationResult;
import at.ac.ubik.archadvisor.domain.Technology;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class BackendOnlyStrategy {

    public RecommendationResult suggest(RecommendationContext ctx, List<Technology> catalog) {
        //TODO: Add functionality
        return null;
    }
}
