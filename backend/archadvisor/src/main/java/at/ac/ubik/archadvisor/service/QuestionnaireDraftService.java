package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.DTO.QuestionnaireDraftDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireDraftKeyDto;
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
        questionnaireDraftRepository.findFirstByKeyDraftIdOrderByKeyVersionDesc(id)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire draft not found: " + id));
    }

    @Transactional
    public QuestionnaireDraftKeyDto createDraft(QuestionnaireRequestDto dto) {
        UUID uuid = UUID.randomUUID();
        questionnaireDraftHeadRepository.ensureHeadExists(uuid);
        long version = questionnaireDraftHeadRepository.allocateNextVersion(uuid);
        QuestionnaireDraftEntity questionnaireDraft = questionnaireDraftRepository.save(mapper.toEntity(dto, uuid, version));
        return new QuestionnaireDraftKeyDto(uuid, version);
    }

    @Transactional()
    public QuestionnaireDraftDto getLatestDraft(UUID draftId) {
        QuestionnaireDraftEntity e = questionnaireDraftRepository.findFirstByKeyDraftIdOrderByKeyVersionDesc(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire draft not found: " + draftId));
        QuestionnaireRequestDto questionnaireRequestDto = mapper.toDto(e);
        return new QuestionnaireDraftDto(draftId, e.getKey().getVersion(), questionnaireRequestDto);
    }

    @Transactional
    public QuestionnaireDraftDto getDraft(UUID draftId, long version) {
        QuestionnaireDraftEntity e = questionnaireDraftRepository.findByKeyDraftIdAndKeyVersion(draftId, version)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire draft not found: " + draftId + " and version: " + version));
        QuestionnaireRequestDto questionnaireRequestDto = mapper.toDto(e);
        return new QuestionnaireDraftDto(draftId, e.getKey().getVersion(), questionnaireRequestDto);

    }

    @Transactional
    public QuestionnaireDraftKeyDto addDraftVersion(UUID draftId, QuestionnaireRequestDto dto) {
        requireDraft(draftId);
        questionnaireDraftHeadRepository.ensureHeadExists(draftId);

        long version = questionnaireDraftHeadRepository.allocateNextVersion(draftId);
        questionnaireDraftRepository.save(mapper.toEntity(dto, draftId, version));
        return new QuestionnaireDraftKeyDto(draftId, version);
    }

}
