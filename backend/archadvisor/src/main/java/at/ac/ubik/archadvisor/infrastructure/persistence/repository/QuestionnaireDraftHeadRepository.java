package at.ac.ubik.archadvisor.infrastructure.persistence.repository;

import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftHeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface QuestionnaireDraftHeadRepository
        extends JpaRepository<QuestionnaireDraftHeadEntity, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO questionnaire_draft_heads(draft_id, latest_version)
            VALUES (:draftId, 0)
            ON CONFLICT (draft_id) DO NOTHING
            """, nativeQuery = true)
    void ensureHeadExists(@Param("draftId") UUID draftId);

    @Query(value = """
            UPDATE questionnaire_draft_heads
            SET latest_version = latest_version + 1
            WHERE draft_id = :draftId
            RETURNING latest_version
            """, nativeQuery = true)
    long allocateNextVersion(@Param("draftId") UUID draftId);
}
