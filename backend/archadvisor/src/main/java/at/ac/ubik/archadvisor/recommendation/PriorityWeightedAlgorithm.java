package at.ac.ubik.archadvisor.recommendation;

import at.ac.ubik.archadvisor.domain.*;
import at.ac.ubik.archadvisor.domain.enums.PriorityAspect;
import org.springframework.stereotype.Component;

@Component
public class PriorityWeightedAlgorithm implements ISuggestionAlgorithm {


    public double getScore(Technology tech, RecommendationContext ctx) {
        PriorityRanking priorityRanking = ctx.getPriorityRanking();
        long N = priorityRanking.getRankByAspect().size();
        double sum = 0.0;
        for (PriorityAspect aspect : priorityRanking.getRankByAspect().keySet()) {
            double weight = calculateWeight(priorityRanking.getRankByAspect().get(aspect), N);
            sum += weight * tech.getMetricScore(aspect);
        }

        TeamProfile team = ctx.getTeamProfile();
        if (tech instanceof BackendFramework backend &&
                team.getFamiliarLanguages().contains(backend.getProgrammingLanguage())) {
            sum += 0.05;
        }

        return Math.max(0.0, Math.min(1.0, sum));
    }

    private double calculateWeight(int rank, long N) {
        double sum = 0.0;
        for (double i = rank; i <= N; i++) {
            sum += 1 / i;
        }
        return 1.0 / N * sum;

    }
}
