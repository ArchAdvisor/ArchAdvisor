package at.ac.ubik.archadvisor.DTO;

import at.ac.ubik.archadvisor.domain.enums.*;

import java.util.List;
import java.util.Set;

public class QuestionnaireRequestDto {

    private ArchitectureScope architectureScope;
    private boolean isOpenSource;
    private DeploymentPreference deploymentPreference;
    private BudgetTier budgetTier;
    private Long expectedUsers;
    private boolean isServerlessFriendly;
    private Integer teamSize;
    private String experienceLevel;
    private Set<ProgrammingLanguage> programmingLanguages;
    private List<PriorityAspect> priorityAspects;

    public QuestionnaireRequestDto() {
    }

    public Set<ProgrammingLanguage> getProgrammingLanguages() {
        return programmingLanguages;
    }

    public void setProgrammingLanguages(Set<ProgrammingLanguage> programmingLanguages) {
        this.programmingLanguages = programmingLanguages;
    }

    public boolean isServerlessFriendly() {
        return isServerlessFriendly;
    }

    public void setServerlessFriendly(boolean serverlessFriendly) {
        isServerlessFriendly = serverlessFriendly;
    }

    public BudgetTier getBudgetTier() {
        return budgetTier;
    }

    public void setBudgetTier(BudgetTier budgetTier) {
        this.budgetTier = budgetTier;
    }

    public ArchitectureScope getArchitectureScope() {
        return architectureScope;
    }

    public void setArchitectureScope(ArchitectureScope architectureScope) {
        this.architectureScope = architectureScope;
    }

    public boolean isIsOpenSource() {
        return isOpenSource;
    }

    public void setIsOpenSource(boolean openSource) {
        isOpenSource = openSource;
    }

    public DeploymentPreference getDeploymentPreference() {
        return deploymentPreference;
    }

    public void setDeploymentPreference(DeploymentPreference deploymentPreference) {
        this.deploymentPreference = deploymentPreference;
    }

    public Long getExpectedUsers() {
        return expectedUsers;
    }

    public void setExpectedUsers(Long expectedUsers) {
        this.expectedUsers = expectedUsers;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }


    public List<PriorityAspect> getPriorityAspects() {
        return priorityAspects;
    }

    public void setPriorityAspects(List<PriorityAspect> priorityAspects) {
        this.priorityAspects = priorityAspects;
    }
}

