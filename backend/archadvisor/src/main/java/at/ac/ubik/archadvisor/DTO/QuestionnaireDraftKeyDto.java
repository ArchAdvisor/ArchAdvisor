package at.ac.ubik.archadvisor.DTO;

import java.util.UUID;

public record QuestionnaireDraftKeyDto(
        UUID draftId,
        long version) {
}
