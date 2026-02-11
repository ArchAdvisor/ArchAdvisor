package at.ac.ubik.archadvisor.mapper;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class QuestionnaireDraftMapper {
    private final ObjectMapper objectMapper;

    public QuestionnaireDraftMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public QuestionnaireDraftEntity toEntity(QuestionnaireRequestDto dto, UUID uuid, long version) {
        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        entity.setKey(new QuestionnaireDraftKey(uuid, version));
        entity.setPayload(toJson(dto));
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
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

    public QuestionnaireRequestDto payloadToDto(QuestionnaireDraftEntity entity) {
        return objectMapper.convertValue(
                entity.getPayload(),
                QuestionnaireRequestDto.class
        );
    }

}
