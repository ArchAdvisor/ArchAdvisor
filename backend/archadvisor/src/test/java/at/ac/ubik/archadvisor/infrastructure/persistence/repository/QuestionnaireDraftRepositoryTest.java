package at.ac.ubik.archadvisor.infrastructure.persistence.repository;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.domain.enums.PriorityAspect;
import at.ac.ubik.archadvisor.domain.enums.ProgrammingLanguage;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Workaround: Had to use Testcontainers here, because H2 db would not work.
 * It could not create the questionnaire_draft_table.
 * This only occurred after adding the composite keys via @Embedded.
 **/
@DataJpaTest
@Testcontainers
class QuestionnaireDraftRepositoryTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private QuestionnaireDraftRepository repository;


    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("archadvisor_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        r.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");

        // choose one:
        r.add("spring.flyway.enabled", () -> "false");
        r.add("spring.liquibase.enabled", () -> "false");
    }

    @Test
    void saveAndFindById_persistsJsonPayloadAndMetadata() {
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();
        dto.setArchitectureScope(ArchitectureScope.BACKEND_ONLY);
        dto.setOpenSource(false);
        dto.setDeploymentPreference(null);
        dto.setBudgetTier(null);
        dto.setExpectedUsers(null);
        dto.setServerlessFriendly(true);
        dto.setTeamSize(3);
        dto.setExperienceLevel("Beginner");
        dto.setProgrammingLanguages(Set.of(ProgrammingLanguage.JAVA, ProgrammingLanguage.JAVASCRIPT));
        dto.setPriorityAspects(List.of(
                PriorityAspect.PERFORMANCE,
                PriorityAspect.SCALABILITY,
                PriorityAspect.SECURITY
        ));
        dto.setTopRankN(4);

        JsonNode payload = objectMapper.valueToTree(dto);

        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        entity.setPayload(payload);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setKey(new QuestionnaireDraftKey(UUID.randomUUID(), 1));

        QuestionnaireDraftEntity saved = repository.save(entity);
        UUID id = saved.getKey().getDraftId();

        assertThat(id).isNotNull();

        QuestionnaireDraftEntity reloaded = repository.findByKeyDraftIdAndKeyVersion(id, 1L).orElseThrow();
        assertThat(reloaded.getPayload()).isNotNull();
        assertThat(reloaded.getKey().getVersion()).isEqualTo(1);
        assertThat(reloaded.getCreatedAt()).isNotNull();
        assertThat(reloaded.getUpdatedAt()).isNotNull();

        assertThat(reloaded.getPayload().get("architectureScope").asText()).isEqualTo("BACKEND_ONLY");
        assertThat(reloaded.getPayload().get("topRankN").asInt()).isEqualTo(4);
    }

    @Test
    void updateEntity_persistsNewPayloadAndVersion() {
        QuestionnaireRequestDto dto1 = new QuestionnaireRequestDto();
        dto1.setArchitectureScope(ArchitectureScope.BACKEND_ONLY);
        dto1.setOpenSource(false);
        dto1.setServerlessFriendly(false);
        dto1.setProgrammingLanguages(Set.of(ProgrammingLanguage.JAVA));
        dto1.setPriorityAspects(List.of(PriorityAspect.PERFORMANCE));
        dto1.setTopRankN(2);

        UUID draftId = UUID.randomUUID();

        QuestionnaireDraftEntity v1 = new QuestionnaireDraftEntity();
        v1.setKey(new QuestionnaireDraftKey(draftId, 1));
        v1.setPayload(objectMapper.valueToTree(dto1));
        v1.setCreatedAt(Instant.now());
        v1.setUpdatedAt(Instant.now());

        repository.save(v1);


        QuestionnaireRequestDto dto2 = new QuestionnaireRequestDto();
        dto2.setArchitectureScope(ArchitectureScope.FULL_STACK);
        dto2.setOpenSource(true);
        dto2.setServerlessFriendly(true);
        dto2.setProgrammingLanguages(Set.of(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.PYTHON));
        dto2.setPriorityAspects(List.of(PriorityAspect.SECURITY, PriorityAspect.MAINTAINABILITY));
        dto2.setTopRankN(5);

        QuestionnaireDraftEntity v2 = new QuestionnaireDraftEntity();
        v2.setKey(new QuestionnaireDraftKey(draftId, 2));
        v2.setPayload(objectMapper.valueToTree(dto2));
        v2.setCreatedAt(Instant.now());
        v2.setUpdatedAt(Instant.now());

        repository.save(v2);

        QuestionnaireDraftEntity reloaded =
                repository.findByKeyDraftIdAndKeyVersion(draftId, 2).orElseThrow();

        assertThat(reloaded.getKey().getDraftId()).isEqualTo(draftId);
        assertThat(reloaded.getKey().getVersion()).isEqualTo(2);
        assertThat(reloaded.getPayload().get("architectureScope").asText()).isEqualTo("FULL_STACK");
        assertThat(reloaded.getPayload().get("topRankN").asInt()).isEqualTo(5);
    }
}