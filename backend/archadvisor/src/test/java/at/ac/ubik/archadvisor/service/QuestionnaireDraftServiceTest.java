package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.DTO.QuestionnaireDraftDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireDraftKeyDto;
import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftKey;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftHeadRepository;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftRepository;
import at.ac.ubik.archadvisor.mapper.QuestionnaireDraftMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireDraftServiceTest {

    @Mock
    QuestionnaireDraftMapper mapper;

    @Mock
    QuestionnaireDraftRepository repository;

    @Mock
    QuestionnaireDraftHeadRepository repositoryHead;

    @InjectMocks
    QuestionnaireDraftService service;

    @Test
    void createDraft_savesEntity_andReturnsId() {
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();
        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        entity.setKey(new QuestionnaireDraftKey());

        when(repositoryHead.allocateNextVersion(any(UUID.class))).thenReturn(1L);
        when(mapper.toEntity(eq(dto), any(UUID.class), eq(1L))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        QuestionnaireDraftKeyDto result = service.createDraft(dto);

        assertThat(result).isNotNull();

        ArgumentCaptor<UUID> draftIdCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(repositoryHead).allocateNextVersion(draftIdCaptor.capture());
        verify(repositoryHead).ensureHeadExists(draftIdCaptor.capture());
        UUID usedDraftId = draftIdCaptor.getValue();

        verify(mapper).toEntity(dto, usedDraftId, 1L);
        verify(repository).save(entity);

        assertThat(result.draftId()).isEqualTo(usedDraftId);

        verifyNoMoreInteractions(mapper, repository, repositoryHead);
    }

    @Test
    void getDraft_whenFound_returnsDto() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        entity.setKey(new QuestionnaireDraftKey(id, 1L));
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();

        when(repository.findFirstByKeyDraftIdOrderByKeyVersionDesc(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        QuestionnaireDraftDto result = service.getLatestDraft(id);

        assertThat(result.payload().getTeamSize()).isSameAs(dto.getTeamSize());
        verify(repository).findFirstByKeyDraftIdOrderByKeyVersionDesc(id);
        verify(mapper).toDto(entity);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void getDraft_whenNotFound_throwsEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findFirstByKeyDraftIdOrderByKeyVersionDesc(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLatestDraft(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Questionnaire draft not found");

        verify(repository).findFirstByKeyDraftIdOrderByKeyVersionDesc(id);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void addDraftVersion_whenFound_allocatesNextVersion_saves_andReturnsNewVersion() {
        UUID draftId = UUID.randomUUID();
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();

        long newVersion = 2L;

        QuestionnaireDraftEntity toSave = new QuestionnaireDraftEntity();
        toSave.setKey(new QuestionnaireDraftKey(draftId, newVersion));

        //when(repositoryHead.existsById(draftId)).thenReturn(true);
        when(repositoryHead.allocateNextVersion(draftId)).thenReturn(newVersion);
        when(mapper.toEntity(dto, draftId, newVersion)).thenReturn(toSave);
        when(repository.save(toSave)).thenReturn(toSave);
        when(repository.findFirstByKeyDraftIdOrderByKeyVersionDesc(draftId)).thenReturn(Optional.of(toSave));

        QuestionnaireDraftKeyDto result = service.addDraftVersion(draftId, dto);

        assertThat(result.version()).isEqualTo(newVersion);
        assertThat(result.draftId()).isEqualTo(draftId);
        verify(repositoryHead).allocateNextVersion(draftId);
        verify(repositoryHead).ensureHeadExists(draftId);
        verify(mapper).toEntity(dto, draftId, newVersion);
        verify(repository).save(toSave);
        verifyNoMoreInteractions(repositoryHead, repository, mapper);
    }

    @Test
    void addDraftVersion_whenNotFound_throwsEntityNotFound() {
        UUID draftId = UUID.randomUUID();
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();


        assertThatThrownBy(() -> service.addDraftVersion(draftId, dto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository).findFirstByKeyDraftIdOrderByKeyVersionDesc(draftId);
        verifyNoMoreInteractions(repositoryHead, repository, mapper);
    }

    @Test
    void getDraft_whenFound_returnDto() {
        UUID id = UUID.randomUUID();
        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        entity.setKey(new QuestionnaireDraftKey(id, 1L));
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();

        when(repository.findByKeyDraftIdAndKeyVersion(id, 2L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        QuestionnaireDraftDto result = service.getDraft(id, 2L);

        assertThat(result.payload()).isSameAs(dto);
        verify(repository).findByKeyDraftIdAndKeyVersion(id, 2L);
        verify(mapper).toDto(entity);
        verifyNoMoreInteractions(mapper, repository);

    }
}