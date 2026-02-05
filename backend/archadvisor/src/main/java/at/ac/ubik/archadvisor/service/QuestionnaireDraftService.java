package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftHeadRepository;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftRepository;
import at.ac.ubik.archadvisor.mapper.QuestionnaireDraftMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuestionnaireDraftService {
    private final QuestionnaireDraftMapper mapper;

    private final QuestionnaireDraftRepository questionnaireDraftRepository;
    private final QuestionnaireDraftHeadRepository questionnaireDraftHeadRepository;

    public QuestionnaireDraftService(QuestionnaireDraftMapper mapper, QuestionnaireDraftRepository questionnaireDraftRepository, QuestionnaireDraftHeadRepository questionnaireDraftHeadRepository) {
        this.mapper = mapper;
        this.questionnaireDraftRepository = questionnaireDraftRepository;
        this.questionnaireDraftHeadRepository = questionnaireDraftHeadRepository;
    }

    private void requireDraft(UUID id) {
        questionnaireDraftRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire draft not found: " + id));
    }

    @Transactional
    public UUID createDraft(QuestionnaireRequestDto dto) {
        UUID uuid = UUID.randomUUID();
        questionnaireDraftHeadRepository.ensureHeadExists(uuid);
        long version = questionnaireDraftHeadRepository.allocateNextVersion(uuid);
        QuestionnaireDraftEntity questionnaireDraft = questionnaireDraftRepository.save(mapper.toEntity(dto, uuid, version));
        return uuid;
    }

    @Transactional()
    public QuestionnaireRequestDto getLatestDraft(UUID draftId) {
        QuestionnaireDraftEntity e = questionnaireDraftRepository.findFirstByKeyDraftIdOrderByKeyVersionDesc(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire draft not found: " + draftId));
        return mapper.toDto(e);
    }

    @Transactional
    public QuestionnaireRequestDto getDraft(UUID draftId, long version) {
        QuestionnaireDraftEntity e = questionnaireDraftRepository.findByKeyDraftIdAndKeyVersion(draftId, version)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire draft not found: " + draftId + " and version: " + version));
        return mapper.toDto(e);

    }

    @Transactional
    public long addDraftVersion(UUID draftId, QuestionnaireRequestDto dto) {
        requireDraft(draftId);
        questionnaireDraftHeadRepository.ensureHeadExists(draftId);

        long version = questionnaireDraftHeadRepository.allocateNextVersion(draftId);
        questionnaireDraftRepository.save(mapper.toEntity(dto, draftId, version));
        return version;
    }

}
