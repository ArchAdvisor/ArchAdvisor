package at.ac.ubik.archadvisor.recommendation;

import at.ac.ubik.archadvisor.domain.RecommendationContext;
import at.ac.ubik.archadvisor.domain.Technology;

public interface ISuggestionAlgorithm {
    double getScore(Technology tech, RecommendationContext ctx);
}
