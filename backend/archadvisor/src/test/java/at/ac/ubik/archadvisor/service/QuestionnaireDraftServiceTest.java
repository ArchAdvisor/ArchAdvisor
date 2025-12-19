package at.ac.ubik.archadvisor.service;

import at.ac.ubik.archadvisor.DTO.QuestionnaireRequestDto;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.QuestionnaireDraftEntity;
import at.ac.ubik.archadvisor.infrastructure.persistence.repository.QuestionnaireDraftRepository;
import at.ac.ubik.archadvisor.mapper.QuestionnaireDraftMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    QuestionnaireDraftService service;

    @Test
    void createDraft_savesEntity_andReturnsId() {
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();

        UUID id = UUID.randomUUID();
        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        entity.setId(id);

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        UUID result = service.createDraft(dto);

        assertThat(result).isEqualTo(id);
        verify(mapper).toEntity(dto);
        verify(repository).save(entity);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void getDraft_whenFound_returnsDto() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionnaireDraftEntity entity = new QuestionnaireDraftEntity();
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        QuestionnaireRequestDto result = service.getDraft(id);

        assertThat(result).isSameAs(dto);
        verify(repository).findById(id);
        verify(mapper).toDto(entity);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void getDraft_whenNotFound_throwsEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDraft(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Questionnaire draft not found");

        verify(repository).findById(id);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void updateDraft_whenFound_updatesEntity_saves_andReturnsSameId() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionnaireDraftEntity existing = new QuestionnaireDraftEntity();
        existing.setId(id);

        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        UUID result = service.updateDraft(id, dto);

        assertThat(result).isEqualTo(id);
        verify(repository).findById(id);
        verify(mapper).updateEntity(existing, dto);
        verify(repository).save(existing);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void updateDraft_whenNotFound_throwsEntityNotFound() {
        UUID id = UUID.randomUUID();
        QuestionnaireRequestDto dto = new QuestionnaireRequestDto();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateDraft(id, dto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository).findById(id);
        verifyNoMoreInteractions(mapper, repository);
    }
}