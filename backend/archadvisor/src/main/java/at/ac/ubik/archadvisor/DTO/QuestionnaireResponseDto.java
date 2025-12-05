package at.ac.ubik.archadvisor.DTO;

import at.ac.ubik.archadvisor.domain.Recommendation;
import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;

import java.util.List;

public class QuestionnaireResponseDto {

    private ArchitectureScope architectureScope;
    private List<Recommendation> backends;
    private List<Recommendation> frontends;
    private List<Recommendation> databases;
    private List<Recommendation> mobileFrameworks;

    public void setArchitectureScope(ArchitectureScope architectureScope) {
        this.architectureScope = architectureScope;
    }

    public List<Recommendation> getMobileFrameworks() {
        return mobileFrameworks;
    }

    public void setMobileFrameworks(List<Recommendation> mobileFrameworks) {
        this.mobileFrameworks = mobileFrameworks;
    }

    public List<Recommendation> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Recommendation> databases) {
        this.databases = databases;
    }

    public List<Recommendation> getFrontends() {
        return frontends;
    }

    public void setFrontends(List<Recommendation> frontends) {
        this.frontends = frontends;
    }

    public List<Recommendation> getBackends() {
        return backends;
    }

    public void setBackends(List<Recommendation> backends) {
        this.backends = backends;
    }

    public QuestionnaireResponseDto(ArchitectureScope architectureScope) {
        this.architectureScope = architectureScope;
    }

    public QuestionnaireResponseDto() {
    }

    public ArchitectureScope getArchitectureScope() {
        return architectureScope;
    }
}
