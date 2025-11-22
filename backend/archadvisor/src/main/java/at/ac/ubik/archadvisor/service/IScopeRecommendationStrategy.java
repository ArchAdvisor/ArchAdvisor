package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.domain.RecommendationContext;
import at.ac.ubik.archadvisor.domain.RecommendationResult;
import at.ac.ubik.archadvisor.domain.Technology;

import java.util.List;

public interface IScopeRecommendationStrategy {
    RecommendationResult suggest(RecommendationContext recommendationContext, List<Technology> catalog);
}
