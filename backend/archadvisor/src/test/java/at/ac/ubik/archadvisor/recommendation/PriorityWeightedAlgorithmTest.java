package at.ac.ubik.archadvisor.recommendation;

import at.ac.ubik.archadvisor.domain.FrontendFramework;
import at.ac.ubik.archadvisor.domain.PriorityRanking;
import at.ac.ubik.archadvisor.domain.RecommendationContext;
import at.ac.ubik.archadvisor.domain.enums.LicenseType;
import at.ac.ubik.archadvisor.domain.enums.PriorityAspect;
import at.ac.ubik.archadvisor.domain.enums.ProgrammingLanguage;
import at.ac.ubik.archadvisor.domain.enums.RuntimeType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("test")
class PriorityWeightedAlgorithmTest {

    @Test
    void getScore() {
        FrontendFramework react = new FrontendFramework(1L, "react", "test-descp", LicenseType.APACHE_2_0, new HashSet<>(), "test/url", "test/url", Instant.now(), ProgrammingLanguage.JAVASCRIPT, RuntimeType.NODE, true);
        react.setValueOfMetricScore(PriorityAspect.PERFORMANCE, 0.8);
        react.setValueOfMetricScore(PriorityAspect.SCALABILITY, 0.6);
        react.setValueOfMetricScore(PriorityAspect.COST_EFFECTIVENESS, 0.4);

        FrontendFramework angular = new FrontendFramework(2L, "angular", "test-descp", LicenseType.APACHE_2_0, new HashSet<>(), "test/url", "test/url", Instant.now(), ProgrammingLanguage.TYPESCRIPT, RuntimeType.NODE, true);
        angular.setValueOfMetricScore(PriorityAspect.PERFORMANCE, 0.6);
        angular.setValueOfMetricScore(PriorityAspect.SCALABILITY, 0.8);
        angular.setValueOfMetricScore(PriorityAspect.COST_EFFECTIVENESS, 0.7);
        PriorityWeightedAlgorithm priorityWeightedAlgorithm = new PriorityWeightedAlgorithm();
        HashMap<PriorityAspect, Integer> priorities = new HashMap<>();
        priorities.put(PriorityAspect.PERFORMANCE, 1);
        priorities.put(PriorityAspect.SCALABILITY, 2);
        priorities.put(PriorityAspect.COST_EFFECTIVENESS, 3);
        PriorityRanking priorityRanking = new PriorityRanking(priorities);
        RecommendationContext recommendationContext = new RecommendationContext(null, null, priorityRanking);
        double scoreReact = priorityWeightedAlgorithm.getScore(react, recommendationContext);
        double scoreAngular = priorityWeightedAlgorithm.getScore(angular, recommendationContext);

        System.out.println(scoreReact);
        System.out.println(scoreAngular);
        assertEquals(0.7, scoreReact, 0.001);
        assertEquals(0.6667, scoreAngular, 0.001);
    }
}