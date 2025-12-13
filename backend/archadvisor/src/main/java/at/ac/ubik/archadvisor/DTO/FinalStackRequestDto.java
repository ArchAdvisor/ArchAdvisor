package at.ac.ubik.archadvisor.DTO;

import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;

public class FinalStackRequestDto {
    private ArchitectureScope architectureScope;
    private Long backendId;
    private Long frontendId;
    private Long databaseId;
    private Long mobileId;

    public ArchitectureScope getArchitectureScope() {
        return architectureScope;
    }

    public void setArchitectureScope(ArchitectureScope architectureScope) {
        this.architectureScope = architectureScope;
    }

    public Long getBackendId() {
        return backendId;
    }

    public void setBackendId(Long backendId) {
        this.backendId = backendId;
    }

    public Long getFrontendId() {
        return frontendId;
    }

    public void setFrontendId(Long frontendId) {
        this.frontendId = frontendId;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public Long getMobileId() {
        return mobileId;
    }

    public void setMobileId(Long mobileId) {
        this.mobileId = mobileId;
    }
}
