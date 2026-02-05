package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.QuestionnaireDraftDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireDraftKeyDto;
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


    @PostMapping("/createDraft")
    public ResponseEntity<QuestionnaireDraftKeyDto> saveQuestionnaireDraft(@RequestBody QuestionnaireRequestDto dto) {
        QuestionnaireDraftKeyDto questionnaireDraftKeyDto = questionnaireDraftService.createDraft(dto);
        return new ResponseEntity<>(questionnaireDraftKeyDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/latest")
    public ResponseEntity<QuestionnaireDraftDto> getLatestQuestionnaireDraft(@PathVariable UUID id) {

        QuestionnaireDraftDto questionnaireDraftDto = questionnaireDraftService.getLatestDraft(id);
        return new ResponseEntity<>(questionnaireDraftDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/{versionNumber}")
    public ResponseEntity<QuestionnaireDraftDto> getQuestionnaireDraft(@PathVariable UUID id, @PathVariable long versionNumber) {
        QuestionnaireDraftDto questionnaireDraftDto = questionnaireDraftService.getDraft(id, versionNumber);
        return new ResponseEntity<>(questionnaireDraftDto, HttpStatus.OK);
    }

    @PutMapping("/createDraftVersion/{id}")
    public ResponseEntity<QuestionnaireDraftKeyDto> updateQuestionnaireDraft(@RequestBody QuestionnaireRequestDto dto, @PathVariable UUID id) {
        QuestionnaireDraftKeyDto questionnaireDraftKeyDto = questionnaireDraftService.addDraftVersion(id, dto);
        return ResponseEntity.ok(questionnaireDraftKeyDto);
    }

}


