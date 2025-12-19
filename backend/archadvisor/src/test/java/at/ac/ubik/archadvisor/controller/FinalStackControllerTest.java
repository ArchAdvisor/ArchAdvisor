package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.FinalStackRequestDto;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftRepository;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.TechnologyRepository;
import at.ac.ubik.archadvisor.mapper.QuestionnaireDraftMapper;
import at.ac.ubik.archadvisor.service.documentcreator.DocumentCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
        // given
        byte[] fakePdf = "%PDF-1.4\nfake\n%%EOF".getBytes();
        when(documentCreator.createStackPdf(any(), any(), any(), any(), any(), any()))
                .thenReturn(fakePdf);

        TechnologyEntity backend = new TechnologyEntity();
        backend.setName("Spring Boot");

        TechnologyEntity frontend = new TechnologyEntity();
        frontend.setName("React");

        when(technologyRepository.findById(1L)).thenReturn(Optional.of(backend));
        when(technologyRepository.findById(2L)).thenReturn(Optional.of(frontend));
        when(technologyRepository.findById(3L)).thenReturn(Optional.empty()); // database unknown
        // mobile id null -> resolveName not called

        FinalStackRequestDto dto = new FinalStackRequestDto();
        dto.setBackendId(1L);
        dto.setFrontendId(2L);
        dto.setDatabaseId(3L);
        dto.setMobileId(null);
        dto.setArchitectureScope(ArchitectureScope.FULL_STACK);
        dto.setDraftId(UUID.randomUUID().toString());

        String json = objectMapper.writeValueAsString(dto);

        mvc.perform(post("/api/stack/pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("filename=\"archadvisor-stack.pdf\"")))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, containsString("no-cache")))
                .andExpect(content().bytes(fakePdf));

        verify(technologyRepository).findById(1L);
        verify(technologyRepository).findById(2L);
        verify(technologyRepository).findById(3L);
        verify(technologyRepository, never()).findById(ArgumentMatchers.eq(null));

        verify(documentCreator).createStackPdf(
                eq("ArchAdvisor – Recommended Stack"),
                eq("FULL_STACK"),
                eq("Spring Boot"),
                eq("React"),
                eq("Unknown (id=3)"),
                isNull()
        );

        verifyNoMoreInteractions(documentCreator);
    }

    @Test
    void generatePdf_whenArchitectureScopeNull_usesNA() throws Exception {
        byte[] fakePdf = "pdf".getBytes();
        when(documentCreator.createStackPdf(any(), any(), any(), any(), any(), any()))
                .thenReturn(fakePdf);

        FinalStackRequestDto dto = new FinalStackRequestDto();
        dto.setArchitectureScope(null);
        dto.setDraftId(UUID.randomUUID().toString());

        mvc.perform(post("/api/stack/pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE));

        verify(documentCreator).createStackPdf(
                eq("ArchAdvisor – Recommended Stack"),
                eq("N/A"),
                any(), any(), any(), any()
        );
    }
}