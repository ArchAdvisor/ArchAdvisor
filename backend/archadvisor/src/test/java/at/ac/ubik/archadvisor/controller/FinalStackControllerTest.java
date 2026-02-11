package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.FinalStackRequestDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftKey;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftRepository;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.TechnologyRepository;
import at.ac.ubik.archadvisor.mapper.QuestionnaireDraftMapper;
import at.ac.ubik.archadvisor.service.documentcreator.DocumentCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinalStackController.class)
class FinalStackControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TechnologyRepository technologyRepository;
    @MockitoBean
    DocumentCreator documentCreator;

    @MockitoBean
    QuestionnaireDraftRepository questionnaireDraftRepository;

    @MockitoBean
    QuestionnaireDraftMapper questionnaireDraftMapper;

    @Test
    void generatePdf_returnsPdfAndHeaders_andResolvesNames() throws Exception {
        byte[] fakePdf = "%PDF-1.4\nfake\n%%EOF".getBytes();
        when(documentCreator.createStackPdf(any(), any(), any(), any(), any(), any(), anyLong()))
                .thenReturn(fakePdf);

        TechnologyEntity backend = new TechnologyEntity();
        backend.setName("Spring Boot");
        TechnologyEntity frontend = new TechnologyEntity();
        frontend.setName("React");

        when(technologyRepository.findById(1L)).thenReturn(Optional.of(backend));
        when(technologyRepository.findById(2L)).thenReturn(Optional.of(frontend));
        when(technologyRepository.findById(3L)).thenReturn(Optional.empty());

        UUID draftUuid = UUID.randomUUID();
        QuestionnaireDraftEntity draftEntity = new QuestionnaireDraftEntity();
        draftEntity.setKey(new QuestionnaireDraftKey(draftUuid, 7));

        when(questionnaireDraftRepository.findFirstByKeyDraftIdOrderByKeyVersionDesc(draftUuid)).thenReturn(Optional.of(draftEntity));

        QuestionnaireRequestDto questionnaireDto = new QuestionnaireRequestDto();
        questionnaireDto.setProjectName("Demo Project");

        when(questionnaireDraftMapper.payloadToDto(any(QuestionnaireDraftEntity.class)))
                .thenReturn(questionnaireDto);

        FinalStackRequestDto dto = new FinalStackRequestDto();
        dto.setBackendId(1L);
        dto.setFrontendId(2L);
        dto.setDatabaseId(3L);
        dto.setMobileId(null);
        dto.setArchitectureScope(ArchitectureScope.FULL_STACK);
        dto.setDraftId(draftUuid.toString());

        mvc.perform(post("/api/stack/pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("filename=\"archadvisor-stack.pdf\"")))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, containsString("no-cache")))
                .andExpect(content().bytes(fakePdf));

        verify(documentCreator).createStackPdf(
                any(FinalStackRequestDto.class),
                eq("Spring Boot"),
                eq("React"),
                eq("Unknown (id=3)"),
                isNull(),
                eq(questionnaireDto),
                eq(7L)
        );
    }

}