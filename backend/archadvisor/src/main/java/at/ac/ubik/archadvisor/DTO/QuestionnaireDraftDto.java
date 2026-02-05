package at.ac.ubik.archadvisor.DTO;

import java.util.UUID;

public record QuestionnaireDraftDto(
        UUID draftId,
        long version,
        QuestionnaireRequestDto payload
) {
}