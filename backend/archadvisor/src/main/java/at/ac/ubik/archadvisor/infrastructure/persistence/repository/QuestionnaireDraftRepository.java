package at.ac.ubik.archadvisor.infrastructure.persistence.repository;

import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionnaireDraftRepository extends JpaRepository<QuestionnaireDraftEntity, UUID> {
    Optional<QuestionnaireDraftEntity> findFirstByKeyDraftIdOrderByKeyVersionDesc(UUID draftId);

    Optional<QuestionnaireDraftEntity> findByKeyDraftIdAndKeyVersion(UUID draftId, long version);

    List<QuestionnaireDraftEntity> findByKeyDraftIdOrderByKeyVersionDesc(UUID draftId);
}
