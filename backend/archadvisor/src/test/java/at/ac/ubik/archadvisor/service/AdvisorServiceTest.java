package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.domain.*;
import at.ac.ubik.archadvisor.domain.enums.*;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.CompatibilityRuleRepository;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AdvisorServiceTest {

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private CompatibilityRuleRepository compatibilityRuleRepository;

    @Autowired
    private AdvisorService advisorService;


    @BeforeEach
    void setUpRepository() {
        compatibilityRuleRepository.deleteAll();
        technologyRepository.deleteAll();
        TechnologyEntity backend_one = new TechnologyEntity(
                "Spring Boot",
                TechnologyKind.BACKEND,
                Instant.now(),
                "https://spring.io/projects/spring-boot",
                "https://github.com/spring-projects/spring-boot",
                "java,spring,backend,microservices",
                LicenseType.OPEN_SOURCE
        );
        backend_one.setLanguage(ProgrammingLanguage.JAVA);
        backend_one.setRuntime(RuntimeType.JDK);
        backend_one.setServerlessFriendly(false);
        backend_one.setSupportsSSR(false);
        backend_one.setPerformanceScore(0.85);
        backend_one.setScalabilityScore(0.90);
        backend_one.setMaintainabilityScore(0.95);
        backend_one.setSecurityScore(0.90);
        backend_one.setCostEffectivenessScore(0.80);
        backend_one.setCommunitySupportScore(0.95);
        backend_one.setEcosystemMaturityScore(0.98);
        backend_one.setVendorLockinScore(0.10);
        technologyRepository.save(backend_one);

        TechnologyEntity backend_two = new TechnologyEntity(
                "Express.js",
                TechnologyKind.BACKEND,
                Instant.now(),
                "https://expressjs.com/",
                "https://github.com/expressjs/express",
                "nodejs,javascript,backend,rest",
                LicenseType.OPEN_SOURCE
        );
        backend_two.setLanguage(ProgrammingLanguage.JAVASCRIPT);
        backend_two.setRuntime(RuntimeType.NODE);
        backend_two.setServerlessFriendly(true);
        backend_two.setSupportsSSR(false);
        backend_two.setPerformanceScore(0.80);
        backend_two.setScalabilityScore(0.85);
        backend_two.setMaintainabilityScore(0.75);
        backend_two.setSecurityScore(0.70);
        backend_two.setCostEffectivenessScore(0.90);
        backend_two.setCommunitySupportScore(0.88);
        backend_two.setEcosystemMaturityScore(0.90);
        backend_two.setVendorLockinScore(0.05);
        technologyRepository.save(backend_two);


        TechnologyEntity frontend_one = new TechnologyEntity(
                "React",
                TechnologyKind.FRONTEND,
                Instant.now(),
                "https://react.dev/",
                "https://github.com/facebook/react",
                "frontend,react,javascript,web",
                LicenseType.OPEN_SOURCE
        );
        frontend_one.setLanguage(ProgrammingLanguage.JAVASCRIPT);
        frontend_one.setRuntime(RuntimeType.BROWSER);
        frontend_one.setServerlessFriendly(true);
        frontend_one.setSupportsSSR(true);
        frontend_one.setPerformanceScore(0.80);
        frontend_one.setScalabilityScore(0.85);
        frontend_one.setMaintainabilityScore(0.90);
        frontend_one.setSecurityScore(0.70);
        frontend_one.setCostEffectivenessScore(0.95);
        frontend_one.setCommunitySupportScore(0.98);
        frontend_one.setEcosystemMaturityScore(0.97);
        frontend_one.setVendorLockinScore(0.10);
        technologyRepository.save(frontend_one);

        TechnologyEntity frontend_two = new TechnologyEntity(
                "Angular",
                TechnologyKind.FRONTEND,
                Instant.now(),
                "https://angular.io/",
                "https://github.com/angular/angular",
                "typescript,frontend,spa,web",
                LicenseType.OPEN_SOURCE
        );
        frontend_two.setLanguage(ProgrammingLanguage.TYPESCRIPT);
        frontend_two.setRuntime(RuntimeType.BROWSER);
        frontend_two.setServerlessFriendly(true);
        frontend_two.setSupportsSSR(true);
        frontend_two.setPerformanceScore(0.75);
        frontend_two.setScalabilityScore(0.90);
        frontend_two.setMaintainabilityScore(0.85);
        frontend_two.setSecurityScore(0.80);
        frontend_two.setCostEffectivenessScore(0.85);
        frontend_two.setCommunitySupportScore(0.90);
        frontend_two.setEcosystemMaturityScore(0.95);
        frontend_two.setVendorLockinScore(0.20);
        technologyRepository.save(frontend_two);


        TechnologyEntity db_one = new TechnologyEntity(
                "PostgreSQL",
                TechnologyKind.DATABASE,
                Instant.now(),
                "https://www.postgresql.org/docs/",
                "https://github.com/postgres/postgres",
                "database,relational,sql,postgres",
                LicenseType.OPEN_SOURCE
        );
        db_one.setDbModelType(DbModelType.RELATIONAL);
        db_one.setRuntime(RuntimeType.NATIVE);
        db_one.setServerlessFriendly(true);
        db_one.setPerformanceScore(0.90);
        db_one.setScalabilityScore(0.85);
        db_one.setMaintainabilityScore(0.95);
        db_one.setSecurityScore(0.95);
        db_one.setCostEffectivenessScore(0.95);
        db_one.setCommunitySupportScore(0.95);
        db_one.setEcosystemMaturityScore(0.98);
        db_one.setVendorLockinScore(0.05);
        technologyRepository.save(db_one);

        TechnologyEntity db_two = new TechnologyEntity(
                "MongoDB",
                TechnologyKind.DATABASE,
                Instant.now(),
                "https://www.mongodb.com/docs/",
                "https://github.com/mongodb/mongo",
                "database,nosql,mongodb,document",
                LicenseType.OPEN_SOURCE
        );
        db_two.setDbModelType(DbModelType.DOCUMENT);
        db_two.setRuntime(RuntimeType.NATIVE);
        db_two.setServerlessFriendly(true);
        db_two.setPerformanceScore(0.80);
        db_two.setScalabilityScore(0.90);
        db_two.setMaintainabilityScore(0.85);
        db_two.setSecurityScore(0.75);
        db_two.setCostEffectivenessScore(0.85);
        db_two.setCommunitySupportScore(0.90);
        db_two.setEcosystemMaturityScore(0.95);
        db_two.setVendorLockinScore(0.10);
        technologyRepository.save(db_two);

        TechnologyEntity mobile_one = new TechnologyEntity(
                "Flutter",
                TechnologyKind.MOBILE,
                Instant.now(),
                "https://docs.flutter.dev/",
                "https://github.com/flutter/flutter",
                "dart,flutter,mobile,cross-platform",
                LicenseType.OPEN_SOURCE
        );
        mobile_one.setLanguage(ProgrammingLanguage.DART);
        mobile_one.setMobilePlatform(MobilePlatform.IOS_AND_ANDROID);
        mobile_one.setRuntime(RuntimeType.NATIVE);
        mobile_one.setServerlessFriendly(true);
        mobile_one.setPerformanceScore(0.85);
        mobile_one.setScalabilityScore(0.75);
        mobile_one.setMaintainabilityScore(0.80);
        mobile_one.setSecurityScore(0.75);
        mobile_one.setCostEffectivenessScore(0.95);
        mobile_one.setCommunitySupportScore(0.90);
        mobile_one.setEcosystemMaturityScore(0.90);
        mobile_one.setVendorLockinScore(0.20);
        technologyRepository.save(mobile_one);

        TechnologyEntity mobile_two = new TechnologyEntity(
                "React Native",
                TechnologyKind.MOBILE,
                Instant.now(),
                "https://reactnative.dev/docs",
                "https://github.com/facebook/react-native",
                "javascript,react-native,mobile,cross-platform",
                LicenseType.OPEN_SOURCE
        );
        mobile_two.setLanguage(ProgrammingLanguage.JAVASCRIPT);
        mobile_two.setMobilePlatform(MobilePlatform.IOS_AND_ANDROID);
        mobile_two.setRuntime(RuntimeType.NATIVE);
        mobile_two.setServerlessFriendly(true);
        mobile_two.setPerformanceScore(0.80);
        mobile_two.setScalabilityScore(0.70);
        mobile_two.setMaintainabilityScore(0.85);
        mobile_two.setSecurityScore(0.70);
        mobile_two.setCostEffectivenessScore(0.95);
        mobile_two.setCommunitySupportScore(0.90);
        mobile_two.setEcosystemMaturityScore(0.95);
        mobile_two.setVendorLockinScore(0.15);
        technologyRepository.save(mobile_two);
    }

    @Test
    void suggest_Returns2Recommendations_WhenCalledWith2NumberOfCandidatesBackendOnly() {
        TechnicalProfile technicalProfile = new TechnicalProfile(ArchitectureScope.BACKEND_ONLY, true, null, null, true, 100L);
        TeamProfile teamProfile = new TeamProfile(1, "Beginner", new HashSet<>());
        HashMap<PriorityAspect, Integer> priorities = new HashMap<>();
        priorities.put(PriorityAspect.PERFORMANCE, 1);
        priorities.put(PriorityAspect.SCALABILITY, 2);
        priorities.put(PriorityAspect.COST_EFFECTIVENESS, 3);
        PriorityRanking priorityRanking = new PriorityRanking(priorities);
        RecommendationContext recommendation = new RecommendationContext(technicalProfile, teamProfile, priorityRanking);

        RecommendationResult recommendationResult = advisorService.suggest(recommendation, 2);
        assertEquals(ArchitectureScope.BACKEND_ONLY, recommendationResult.architectureScope());
        assertEquals(2, recommendationResult.backends().toArray().length);
    }

    @Test
    void suggest_Returns1Recommendations_WhenCalledWith1NumberOfCandidatesBackendOnly() {
        TechnicalProfile technicalProfile = new TechnicalProfile(ArchitectureScope.BACKEND_ONLY, true, null, null, true, 100L);
        TeamProfile teamProfile = new TeamProfile(1, "Beginner", new HashSet<>());
        HashMap<PriorityAspect, Integer> priorities = new HashMap<>();
        priorities.put(PriorityAspect.PERFORMANCE, 1);
        priorities.put(PriorityAspect.SCALABILITY, 2);
        priorities.put(PriorityAspect.COST_EFFECTIVENESS, 3);
        PriorityRanking priorityRanking = new PriorityRanking(priorities);
        RecommendationContext recommendation = new RecommendationContext(technicalProfile, teamProfile, priorityRanking);

        RecommendationResult recommendationResult = advisorService.suggest(recommendation, 1);
        assertEquals(ArchitectureScope.BACKEND_ONLY, recommendationResult.architectureScope());
        assertEquals(1, recommendationResult.backends().toArray().length);
    }

    @Test
    void suggest_Returns2Recommendations_WhenCalledWith2NumberOfCandidatesFullStack() {
        TechnicalProfile technicalProfile = new TechnicalProfile(ArchitectureScope.FULL_STACK, true, null, null, true, 100L);
        TeamProfile teamProfile = new TeamProfile(1, "Beginner", new HashSet<>());
        HashMap<PriorityAspect, Integer> priorities = new HashMap<>();
        priorities.put(PriorityAspect.PERFORMANCE, 1);
        priorities.put(PriorityAspect.SCALABILITY, 2);
        priorities.put(PriorityAspect.COST_EFFECTIVENESS, 3);
        PriorityRanking priorityRanking = new PriorityRanking(priorities);
        RecommendationContext recommendation = new RecommendationContext(technicalProfile, teamProfile, priorityRanking);

        RecommendationResult recommendationResult = advisorService.suggest(recommendation, 2);
        assertEquals(ArchitectureScope.FULL_STACK, recommendationResult.architectureScope());
        assertEquals(2, recommendationResult.backends().toArray().length);
        assertEquals(2, recommendationResult.frontends().toArray().length);
        assertEquals(2, recommendationResult.databases().toArray().length);
    }

    @Test
    void suggest_Returns2Recommendations_WhenCalledWith2NumberOfCandidatesMobile() {
        TechnicalProfile technicalProfile = new TechnicalProfile(ArchitectureScope.MOBILE, true, null, null, true, 100L);
        TeamProfile teamProfile = new TeamProfile(1, "Beginner", new HashSet<>());
        HashMap<PriorityAspect, Integer> priorities = new HashMap<>();
        priorities.put(PriorityAspect.PERFORMANCE, 1);
        priorities.put(PriorityAspect.SCALABILITY, 2);
        priorities.put(PriorityAspect.COST_EFFECTIVENESS, 3);
        PriorityRanking priorityRanking = new PriorityRanking(priorities);
        RecommendationContext recommendation = new RecommendationContext(technicalProfile, teamProfile, priorityRanking);

        RecommendationResult recommendationResult = advisorService.suggest(recommendation, 2);
        assertEquals(ArchitectureScope.MOBILE, recommendationResult.architectureScope());
        assertEquals(2, recommendationResult.mobileFrameworks().toArray().length);
    }

    @Test
    void filterByTechnicalProfile_ReturnsFalse_WhenIsOpenSourceAndTechnologyIsProprietary() {
        TechnicalProfile technicalProfile = new TechnicalProfile(ArchitectureScope.BACKEND_ONLY, true, null, null, true, 100L);
        FrontendFramework sencha = new FrontendFramework(1L, "Sencha Ext JS", "test-descp", LicenseType.PROPRIETARY, new HashSet<>(), "test/url", "test/url", Instant.now(), ProgrammingLanguage.JAVASCRIPT, RuntimeType.NODE, true);
        RecommendationContext recommendation = new RecommendationContext(technicalProfile, null, null);
        boolean isAccepted = advisorService.filterByTechnicalProfile(sencha, recommendation);
        assertFalse(isAccepted);
    }

    @Test
    void filterByTechnicalProfile_ReturnsTrue_WhenIsOpenSourceAndTechnologyIsOpenSource() {
        TechnicalProfile technicalProfile = new TechnicalProfile(ArchitectureScope.BACKEND_ONLY, true, null, null, false, 100L);
        FrontendFramework react = new FrontendFramework(1L, "Sencha Ext JS", "test-descp", LicenseType.OPEN_SOURCE, new HashSet<>(), "test/url", "test/url", Instant.now(), ProgrammingLanguage.JAVASCRIPT, RuntimeType.NODE, true);
        RecommendationContext recommendation = new RecommendationContext(technicalProfile, null, null);
        boolean isAccepted = advisorService.filterByTechnicalProfile(react, recommendation);
        assertTrue(isAccepted);
    }


}