package at.ac.ubik.archadvisor.controller;

import at.ac.ubik.archadvisor.DTO.QuestionnaireDraftDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireDraftKeyDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.domain.enums.*;
import at.ac.ubik.archadvisor.service.QuestionnaireDraftService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionnaireDraftController.class)
@Import(ApiExceptionHandler.class)
class QuestionnaireDraftControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    QuestionnaireDraftService draftService;

    @Test
    void createDraft_returns201_andUuid() throws Exception {
        UUID id = UUID.randomUUID();
        when(draftService.createDraft(any(QuestionnaireRequestDto.class))).thenReturn(new QuestionnaireDraftKeyDto(id, 1));

        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();
        dto.setArchitectureScope(ArchitectureScope.BACKEND_ONLY);
        dto.setOpenSource(false);
        dto.setServerlessFriendly(true);
        dto.setProgrammingLanguages(Set.of(ProgrammingLanguage.JAVA));
        dto.setPriorityAspects(List.of(PriorityAspect.PERFORMANCE, PriorityAspect.SCALABILITY));
        dto.setTopRankN(4);

        mvc.perform(post("/api/questionnaire-drafts/createDraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.draftId").value(id.toString()));

        verify(draftService).createDraft(any(QuestionnaireRequestDto.class));
        verifyNoMoreInteractions(draftService);
    }

    @Test
    void getDraft_returns200_andDtoJson() throws Exception {
        UUID id = UUID.randomUUID();

        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();
        dto.setArchitectureScope(ArchitectureScope.FULL_STACK);
        dto.setOpenSource(true);
        dto.setDeploymentPreference(DeploymentPreference.KUBERNETES);
        dto.setBudgetTier(BudgetTier.MEDIUM);
        dto.setExpectedUsers(100L);
        dto.setServerlessFriendly(false);
        dto.setTeamSize(3);
        dto.setExperienceLevel("Beginner");
        dto.setProgrammingLanguages(Set.of(ProgrammingLanguage.JAVA, ProgrammingLanguage.JAVASCRIPT));
        dto.setPriorityAspects(List.of(PriorityAspect.SECURITY, PriorityAspect.MAINTAINABILITY));
        dto.setTopRankN(5);

        when(draftService.getLatestDraft(id)).thenReturn(new QuestionnaireDraftDto(id, 1, dto));

        mvc.perform(get("/api/questionnaire-drafts/{id}/latest", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.payload.architectureScope").value("FULL_STACK"))
                .andExpect(jsonPath("$.payload.openSource").value(true))
                .andExpect(jsonPath("$.payload.deploymentPreference").value("KUBERNETES"))
                .andExpect(jsonPath("$.payload.budgetTier").value("MEDIUM"))
                .andExpect(jsonPath("$.payload.expectedUsers").value(100))
                .andExpect(jsonPath("$.payload.serverlessFriendly").value(false))
                .andExpect(jsonPath("$.payload.teamSize").value(3))
                .andExpect(jsonPath("$.payload.experienceLevel").value("Beginner"))
                .andExpect(jsonPath("$.payload.topRankN").value(5));

        verify(draftService).getLatestDraft(id);
        verifyNoMoreInteractions(draftService);
    }

    @Test
    void updateDraft_returns200_andCallsService() throws Exception {
        UUID id = UUID.randomUUID();

        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();
        dto.setArchitectureScope(ArchitectureScope.MOBILE);
        dto.setOpenSource(false);
        dto.setServerlessFriendly(true);
        dto.setTopRankN(2);

        when(draftService.addDraftVersion(eq(id), any(QuestionnaireRequestDto.class))).thenReturn(new QuestionnaireDraftKeyDto(id, 2L));

        mvc.perform(put("/api/questionnaire-drafts/createDraftVersion/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk());

        verify(draftService).addDraftVersion(eq(id), any(QuestionnaireRequestDto.class));
        verifyNoMoreInteractions(draftService);
    }

    @Test
    void getDraft_whenNotFound_returns404_viaAdvice() throws Exception {
        UUID id = UUID.randomUUID();
        when(draftService.getLatestDraft(id))
                .thenThrow(new EntityNotFoundException("Questionnaire draft not found: " + id));

        mvc.perform(get("/api/questionnaire-drafts/{id}/latest", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("not found")));

        verify(draftService).getLatestDraft(id);
        verifyNoMoreInteractions(draftService);
    }

    @Test
    void test_getQuestionnaireDraft() throws Exception {
        UUID id = UUID.randomUUID();

        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();
        dto.setArchitectureScope(ArchitectureScope.FULL_STACK);
        dto.setOpenSource(true);
        dto.setDeploymentPreference(DeploymentPreference.KUBERNETES);
        dto.setBudgetTier(BudgetTier.MEDIUM);
        dto.setExpectedUsers(100L);
        dto.setServerlessFriendly(false);
        dto.setTeamSize(3);
        dto.setExperienceLevel("Beginner");
        dto.setProgrammingLanguages(Set.of(ProgrammingLanguage.JAVA, ProgrammingLanguage.JAVASCRIPT));
        dto.setPriorityAspects(List.of(PriorityAspect.SECURITY, PriorityAspect.MAINTAINABILITY));
        dto.setTopRankN(5);

        when(draftService.getDraft(id, 1L)).thenReturn(new QuestionnaireDraftDto(id, 2, dto));

        mvc.perform(get("/api/questionnaire-drafts/{id}/{versionNumber}", id, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.draftId").value(id.toString()))
                .andExpect(jsonPath("$.version").value(2))
                .andExpect(jsonPath("$.payload.architectureScope").value("FULL_STACK"))
                .andExpect(jsonPath("$.payload.openSource").value(true))
                .andExpect(jsonPath("$.payload.deploymentPreference").value("KUBERNETES"))
                .andExpect(jsonPath("$.payload.budgetTier").value("MEDIUM"))
                .andExpect(jsonPath("$.payload.expectedUsers").value(100))
                .andExpect(jsonPath("$.payload.serverlessFriendly").value(false))
                .andExpect(jsonPath("$.payload.teamSize").value(3))
                .andExpect(jsonPath("$.payload.experienceLevel").value("Beginner"))
                .andExpect(jsonPath("$.payload.topRankN").value(5));

        verify(draftService).getDraft(id, 1L);
        verifyNoMoreInteractions(draftService);
    }
}