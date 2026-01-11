package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.service.QuestionnaireDraftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/questionnaire-drafts")
public class QuestionnaireDraftController {

    private static final Logger log = LoggerFactory.getLogger(QuestionnaireDraftController.class);
    private final QuestionnaireDraftService questionnaireDraftService;


    public QuestionnaireDraftController(QuestionnaireDraftService questionnaireDraftService) {
        this.questionnaireDraftService = questionnaireDraftService;
    }


    @PostMapping
    public ResponseEntity<UUID> saveQuestionnaireDraft(@RequestBody QuestionnaireRequestDto dto) {
        UUID uuid = questionnaireDraftService.createDraft(dto);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionnaireRequestDto> getQuestionnaireDraft(@PathVariable UUID id) {

        QuestionnaireRequestDto questionnaireResponseDto = questionnaireDraftService.getDraft(id);
        return new ResponseEntity<>(questionnaireResponseDto, HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> updateQuestionnaireDraft(@RequestBody QuestionnaireRequestDto dto, @PathVariable UUID id) {
        questionnaireDraftService.updateDraft(id, dto);
        return new ResponseEntity<>(id, HttpStatus.NO_CONTENT);

    }

}


