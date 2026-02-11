package at.ac.ubik.archadvisor.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "questionnaire_draft_heads")
public class QuestionnaireDraftHeadEntity {

    @Id
    @Column(name = "draft_id", nullable = false, updatable = false)
    private UUID draftId;

    @Column(name = "latest_version", nullable = false)
    private long latestVersion;

    public QuestionnaireDraftHeadEntity() {
    }

    public QuestionnaireDraftHeadEntity(UUID draftId, long latestVersion) {
        this.draftId = draftId;
        this.latestVersion = latestVersion;
    }

    public UUID getDraftId() {
        return draftId;
    }

    public long getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(long latestVersion) {
        this.latestVersion = latestVersion;
    }
}
