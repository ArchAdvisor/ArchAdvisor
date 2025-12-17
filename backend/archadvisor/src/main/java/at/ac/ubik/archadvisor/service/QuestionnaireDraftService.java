package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftRepository;
import at.ac.ubik.archadvisor.mapper.QuestionnaireDraftMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuestionnaireDraftService {
    private final QuestionnaireDraftMapper mapper;

    private final QuestionnaireDraftRepository questionnaireDraftRepository;

    public QuestionnaireDraftService(QuestionnaireDraftMapper mapper, QuestionnaireDraftRepository questionnaireDraftRepository) {
        this.mapper = mapper;
        this.questionnaireDraftRepository = questionnaireDraftRepository;
    }

    private QuestionnaireDraftEntity requireDraft(UUID id) {
        return questionnaireDraftRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire draft not found: " + id));
    }

    public UUID createDraft(QuestionnaireRequestDto dto) {
        QuestionnaireDraftEntity questionnaireDraft = questionnaireDraftRepository.save(mapper.toEntity(dto));
        return questionnaireDraft.getId();
    }

    public QuestionnaireRequestDto getDraft(UUID id) throws Exception {
        QuestionnaireDraftEntity questionnaireDraft = requireDraft(id);
        return mapper.toDto(questionnaireDraft);
    }

    public UUID updateDraft(UUID id, QuestionnaireRequestDto dto) throws Exception {
        QuestionnaireDraftEntity existing = requireDraft(id);
        mapper.updateEntity(existing, dto);
        questionnaireDraftRepository.save(existing);
        return existing.getId();
    }
}
