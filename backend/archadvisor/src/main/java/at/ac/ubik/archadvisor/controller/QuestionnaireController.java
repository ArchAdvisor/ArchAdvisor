package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireResponseDto;
import at.ac.ubik.archadvisor.domain.*;
import at.ac.ubik.archadvisor.service.AdvisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

    private static final Logger log = LoggerFactory.getLogger(QuestionnaireController.class);
    private final AdvisorService recommendationService;


    public QuestionnaireController(AdvisorService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<QuestionnaireResponseDto> handleQuestionnaire(@RequestBody QuestionnaireRequestDto dto) {

        TechnicalProfile technicalProfile = new TechnicalProfile(
                dto.getArchitectureScope(),
                dto.isIsOpenSource(),
                dto.getDeploymentPreference(),
                dto.getBudgetTier(),
                dto.isServerlessFriendly(),
                dto.getExpectedUsers()
        );

        TeamProfile teamProfile = new TeamProfile(
                dto.getTeamSize() != null ? dto.getTeamSize() : 0,
                dto.getExperienceLevel(),
                dto.getProgrammingLanguages()
        );

        PriorityRanking priorityRanking = new PriorityRanking(
        );
        for (int i = 0; i < dto.getPriorityAspects().size(); i++) {
            priorityRanking.setRanksOfPriorityAspects(dto.getPriorityAspects().get(i), (i + 1));
        }

        RecommendationContext context = new RecommendationContext(
                technicalProfile,
                teamProfile,
                priorityRanking
        );
        if (dto.getTopRankN() < 1) {
            log.error("topRankN must be > 0");
            dto.setTopRankN(4);
        }
        RecommendationResult result = recommendationService.suggest(context, dto.getTopRankN());
        QuestionnaireResponseDto questionnaireResponseDto = new QuestionnaireResponseDto(result.architectureScope());
        questionnaireResponseDto.setBackends(result.backends());
        questionnaireResponseDto.setFrontends(result.frontends());
        questionnaireResponseDto.setMobileFrameworks(result.mobileFrameworks());
        questionnaireResponseDto.setDatabases(result.databases());
        return ResponseEntity.ok(questionnaireResponseDto);
    }
}
