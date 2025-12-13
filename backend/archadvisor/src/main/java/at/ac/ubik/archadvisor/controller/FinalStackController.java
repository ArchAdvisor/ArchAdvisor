package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.FinalStackRequestDto;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.TechnologyRepository;
import at.ac.ubik.archadvisor.service.documentcreator.DocumentCreator;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stack")
public class FinalStackController {

    private final TechnologyRepository technologyRepository;
    private final DocumentCreator documentCreator;

    public FinalStackController(TechnologyRepository technologyRepository, DocumentCreator documentCreator) {
        this.technologyRepository = technologyRepository;
        this.documentCreator = documentCreator;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(@RequestBody FinalStackRequestDto dto) throws Exception {

        String backendName = resolveName(dto.getBackendId());
        String frontendName = resolveName(dto.getFrontendId());
        String databaseName = resolveName(dto.getDatabaseId());
        String mobileName = resolveName(dto.getMobileId());

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

    //DEBUG!
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

