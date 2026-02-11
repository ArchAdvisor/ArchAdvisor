package at.ac.ubik.archadvisor.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class QuestionnaireDraftKey implements Serializable {

    @Column(name = "draft_id", nullable = false)
    private UUID draftId;

    @Column(name = "version", nullable = false)
    private long version;

    public QuestionnaireDraftKey() {
    }

    public QuestionnaireDraftKey(UUID draftId, long version) {
        this.draftId = draftId;
        this.version = version;
    }

    public UUID getDraftId() {
        return draftId;
    }

    public void setDraftId(UUID draftId) {
        this.draftId = draftId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionnaireDraftKey)) return false;
        QuestionnaireDraftKey that = (QuestionnaireDraftKey) o;
        return version == that.version && Objects.equals(draftId, that.draftId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(draftId, version);
    }
}

