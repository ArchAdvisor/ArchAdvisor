package at.ac.ubik.archadvisor.DTO;

import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;

public class QuestionnaireResponseDto {

    private ArchitectureScope architectureScope;

    public QuestionnaireResponseDto(ArchitectureScope architectureScope) {
        this.architectureScope = architectureScope;
    }

    public QuestionnaireResponseDto() {
    }

    public ArchitectureScope getArchitectureScope() {
        return architectureScope;
    }
}
