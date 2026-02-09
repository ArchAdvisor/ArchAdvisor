package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.FinalStackRequestDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftRepository;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.TechnologyRepository;
import at.ac.ubik.archadvisor.mapper.QuestionnaireDraftMapper;
import at.ac.ubik.archadvisor.service.documentcreator.DocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/stack")
public class FinalStackController {

    private final TechnologyRepository technologyRepository;
    private static final Logger log = LoggerFactory.getLogger(FinalStackController.class);
    private final DocumentCreator documentCreator;
    private final QuestionnaireDraftRepository questionnaireDraftRepository;
    private final QuestionnaireDraftMapper questionnaireDraftMapper;

    public FinalStackController(TechnologyRepository technologyRepository, QuestionnaireDraftRepository questionnaireDraftRepository, DocumentCreator documentCreator, QuestionnaireDraftMapper questionnaireDraftMapper) {
        this.technologyRepository = technologyRepository;
        this.questionnaireDraftRepository = questionnaireDraftRepository;
        this.documentCreator = documentCreator;
        this.questionnaireDraftMapper = questionnaireDraftMapper;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(@RequestBody FinalStackRequestDto dto) throws Exception {

        String backendName = resolveName(dto.getBackendId());
        String frontendName = resolveName(dto.getFrontendId());
        String databaseName = resolveName(dto.getDatabaseId());
        String mobileName = resolveName(dto.getMobileId());
        String draftLink = dto.getDraftLink();
        String draftId = dto.getDraftId();
        long draftVersion;
        QuestionnaireRequestDto questionnaireRequestDto;
        if (questionnaireDraftRepository.findFirstByKeyDraftIdOrderByKeyVersionDesc(UUID.fromString(draftId)).isPresent()) {
            QuestionnaireDraftEntity questionnaireDraftEntity = questionnaireDraftRepository.findFirstByKeyDraftIdOrderByKeyVersionDesc(UUID.fromString(draftId)).get();
            questionnaireRequestDto = questionnaireDraftMapper.payloadToDto(questionnaireDraftEntity);
            draftVersion = questionnaireDraftRepository.findFirstByKeyDraftIdOrderByKeyVersionDesc(UUID.fromString(draftId)).get().getKey().getVersion();
        } else {
            log.error("Could not find questionnaire draft id {}", draftId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (draftLink == null) {
            log.warn("Draft link is null");
            dto.setDraftLink("http://localhost:3000/draft/" + draftId);
        }


        if (questionnaireRequestDto == null) {
            log.error("Questionnaire Request is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        byte[] pdf = documentCreator.createStackPdf(
                dto,
                backendName,
                frontendName,
                databaseName,
                mobileName,
                questionnaireRequestDto,
                draftVersion
        );


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("archadvisor-stack.pdf")
                .build());
        headers.setCacheControl(CacheControl.noCache());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    private String resolveName(Long id) {
        if (id == null) return null;
        return technologyRepository.findById(id).map(TechnologyEntity::getName).orElse("Unknown (id=" + id + ")");
    }

}

