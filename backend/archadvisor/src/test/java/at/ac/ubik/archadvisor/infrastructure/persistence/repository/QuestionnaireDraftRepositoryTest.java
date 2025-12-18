package at.ac.ubik.archadvisor.infrastructure.persistence.repository;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.domain.enums.PriorityAspect;
import at.ac.ubik.archadvisor.domain.enums.ProgrammingLanguage;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuestionnaireDraftRepositoryTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private QuestionnaireDraftRepository repository;

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
        entity.setVersion(1);

        QuestionnaireDraftEntity saved = repository.save(entity);
        UUID id = saved.getId();

        assertThat(id).isNotNull();

        QuestionnaireDraftEntity reloaded = repository.findById(id).orElseThrow();
        assertThat(reloaded.getPayload()).isNotNull();
        assertThat(reloaded.getVersion()).isEqualTo(1);
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

        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        entity.setPayload(objectMapper.valueToTree(dto1));
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setVersion(1);

        QuestionnaireDraftEntity saved = repository.save(entity);
        UUID id = saved.getId();

        QuestionnaireDraftEntity toUpdate = repository.findById(id).orElseThrow();

        QuestionnaireRequestDto dto2 = new QuestionnaireRequestDto();
        dto2.setArchitectureScope(ArchitectureScope.FULL_STACK);
        dto2.setOpenSource(true);
        dto2.setServerlessFriendly(true);
        dto2.setProgrammingLanguages(Set.of(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.PYTHON));
        dto2.setPriorityAspects(List.of(PriorityAspect.SECURITY, PriorityAspect.MAINTAINABILITY));
        dto2.setTopRankN(5);

        toUpdate.setPayload(objectMapper.valueToTree(dto2));
        toUpdate.setUpdatedAt(Instant.now());
        toUpdate.setVersion(2); // mimic your mapper increment

        repository.save(toUpdate);

        QuestionnaireDraftEntity reloaded = repository.findById(id).orElseThrow();
        assertThat(reloaded.getVersion()).isEqualTo(2);
        assertThat(reloaded.getPayload().get("architectureScope").asText()).isEqualTo("FULL_STACK");
        assertThat(reloaded.getPayload().get("topRankN").asInt()).isEqualTo(5);

        assertThat(reloaded.getId()).isEqualTo(id);
    }
}