package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.FinalStackRequestDto;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftRepository;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.TechnologyRepository;
import at.ac.ubik.archadvisor.service.documentcreator.DocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stack")
public class FinalStackController {

    private final TechnologyRepository technologyRepository;
    private static final Logger log = LoggerFactory.getLogger(FinalStackController.class);
    private final DocumentCreator documentCreator;
    private final QuestionnaireDraftRepository questionnaireDraftRepository;

    public FinalStackController(TechnologyRepository technologyRepository, QuestionnaireDraftRepository questionnaireDraftRepository, DocumentCreator documentCreator) {
        this.technologyRepository = technologyRepository;
        this.questionnaireDraftRepository = questionnaireDraftRepository;
        this.documentCreator = documentCreator;
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
        if (questionnaireDraftRepository.findById(UUID.fromString(draftId)).isPresent()) {
            draftVersion = questionnaireDraftRepository.findById(UUID.fromString(draftId)).get().getVersion();
        } else {
            log.error("Could not find questionnaire draft id {}", draftId);
            draftVersion = 1L;
        }
        if (draftLink == null) {
            log.warn("Draft link is null");
            draftLink = "http://localhost:3000/draft/" + draftId;
        }
        log.warn("Draft additions: {} {} {}", draftLink, draftId, draftVersion);
        byte[] pdf = documentCreator.createStackPdf(
                "ArchAdvisor – Recommended Stack",
                dto.getArchitectureScope() != null ? dto.getArchitectureScope().name() : "N/A",
                backendName,
                frontendName,
                databaseName,
                mobileName
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

    //Debug only!
    @GetMapping(value = "/pdf/preview", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> previewPdf(
            @RequestParam(required = false) Long backendId,
            @RequestParam(required = false) Long frontendId,
            @RequestParam(required = false) Long databaseId,
            @RequestParam(required = false) Long mobileId,
            @RequestParam(defaultValue = "FULL_STACK") ArchitectureScope architectureScope
    ) throws Exception {

        FinalStackRequestDto dto = new FinalStackRequestDto();
        dto.setArchitectureScope(architectureScope);
        dto.setBackendId(backendId);
        dto.setFrontendId(frontendId);
        dto.setDatabaseId(databaseId);
        dto.setMobileId(mobileId);

        String backendName = resolveName(dto.getBackendId());
        String frontendName = resolveName(dto.getFrontendId());
        String databaseName = resolveName(dto.getDatabaseId());
        String mobileName = resolveName(dto.getMobileId());

        byte[] pdf = documentCreator.createStackPdf("ArchAdvisor – Chosen tech Stack",
                dto.getArchitectureScope() != null ? dto.getArchitectureScope().name() : "N/A",
                backendName,
                frontendName,
                databaseName,
                mobileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("preview.pdf").build());
        headers.setCacheControl(CacheControl.noCache());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}

