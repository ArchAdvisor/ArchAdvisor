package at.ac.ubik.archadvisor.mapper;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class QuestionnaireDraftMapper {
    private final ObjectMapper objectMapper;

    public QuestionnaireDraftMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public QuestionnaireDraftEntity toEntity(QuestionnaireRequestDto dto) {
        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        entity.setPayload(toJson(dto));
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setVersion(1);
        return entity;
    }

    public QuestionnaireRequestDto toDto(QuestionnaireDraftEntity entity) {
        return fromJson(entity.getPayload());
    }


    private JsonNode toJson(QuestionnaireRequestDto dto) {
        return objectMapper.valueToTree(dto);
    }

    private QuestionnaireRequestDto fromJson(JsonNode node) {
        try {
            return objectMapper.treeToValue(node, QuestionnaireRequestDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize questionnaire payload", e);
        }
    }

    public void updateEntity(QuestionnaireDraftEntity entity, QuestionnaireRequestDto dto) {
        entity.setPayload(objectMapper.valueToTree(dto));
        entity.setUpdatedAt(Instant.now());
        entity.setVersion(entity.getVersion() + 1);
    }

}
