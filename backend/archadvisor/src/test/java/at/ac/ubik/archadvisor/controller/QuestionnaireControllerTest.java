package at.ac.ubik.archadvisor.controller;


import at.ac.ubik.archadvisor.domain.*;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.domain.enums.LicenseType;
import at.ac.ubik.archadvisor.domain.enums.ProgrammingLanguage;
import at.ac.ubik.archadvisor.domain.enums.RuntimeType;
import at.ac.ubik.archadvisor.service.AdvisorService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionnaireController.class)
class QuestionnaireControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private AdvisorService advisorService;


    @Test
    void handleQuestionnaire_withValidInput_returnsOkAndCallsService() throws Exception {
        Technology backend_one = new BackendFramework(1L,
                "Spring Boot",
                "desc",
                LicenseType.APACHE_2_0,
                new HashSet<>(),
                "https://spring.io/projects/spring-boot",
                "https://github.com/spring-projects/spring-boot",
                Instant.now(),
                ProgrammingLanguage.JAVA,
                RuntimeType.JDK,
                true
        );
        // Arrange: mock service behavior
        RecommendationResult mockResult = new RecommendationResult(
                ArchitectureScope.BACKEND_ONLY,
                List.of(new Recommendation(backend_one, 0.9, new ArrayList<>())), null, null, null
        );
        when(advisorService.suggest(any(RecommendationContext.class), anyInt()))
                .thenReturn(mockResult);

        String json = """
                {
                  "architectureScope":"BACKEND_ONLY",
                  "isOpenSource":false,
                  "deploymentPreferences":null,
                  "budgetTier":null,
                  "expectedNumberOfUsers":null,
                  "teamSize":0,
                  "experienceLevel":"",
                  "programmingLanguages":[],
                  "priorityAspects":[
                    "PERFORMANCE","SCALABILITY","MAINTAINABILITY","SECURITY",
                    "COST_EFFECTIVENESS","COMMUNITY_SUPPORT","ECOSYSTEM_MATURITY","VENDOR_LOCKIN_AVOIDANCE"
                  ]
                }
                """;

        mockMvc.perform(post("/api/questionnaire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.architectureScope").value("BACKEND_ONLY"));
        //.andExpect(jsonPath("$.backends[0]").value("Spring Boot"))
        //.andExpect(jsonPath("$.score").value(0.9));

        ArgumentCaptor<RecommendationContext> ctxCaptor =
                ArgumentCaptor.forClass(RecommendationContext.class);
        verify(advisorService).suggest(ctxCaptor.capture(), eq(4L));

        RecommendationContext captured = ctxCaptor.getValue();
        assertThat(captured.getTechnicalProfile().getScope())
                .isEqualTo(ArchitectureScope.BACKEND_ONLY);
    }
}