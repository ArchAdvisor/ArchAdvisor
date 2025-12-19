package at.ac.ubik.archadvisor.DTO;

import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;

public class FinalStackRequestDto {
    private ArchitectureScope architectureScope;
    private Long backendId;
    private Long frontendId;
    private Long databaseId;
    private Long mobileId;
    private String draftLink;
    private String draftId;

    public String getDraftLink() {
        return draftLink;
    }

    public void setDraftLink(String draftLink) {
        this.draftLink = draftLink;
    }

    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

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
