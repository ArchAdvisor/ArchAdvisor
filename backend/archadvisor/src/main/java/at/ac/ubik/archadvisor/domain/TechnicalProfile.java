package at.ac.ubik.archadvisor.domain;

import at.ac.ubik.archadvisor.domain.enums.ArchitectureScope;
import at.ac.ubik.archadvisor.domain.enums.BudgetTier;
import at.ac.ubik.archadvisor.domain.enums.DeploymentPreference;

public final class TechnicalProfile {
    private final ArchitectureScope scope;
    private final boolean wantsOpenSourceOnly;
    private final DeploymentPreference deploymentPreference;
    private final BudgetTier budgetTier;
    private final Long expectedUsers;


    public TechnicalProfile(ArchitectureScope scope,
                            boolean wantsOpenSourceOnly,
                            DeploymentPreference deploymentPreference,
                            BudgetTier budgetTier,
                            Long expectedUsers) {
        this.scope = scope;
        this.wantsOpenSourceOnly = wantsOpenSourceOnly;
        this.deploymentPreference = deploymentPreference;
        this.budgetTier = budgetTier;
        this.expectedUsers = expectedUsers;
    }

    public ArchitectureScope getScope() {
        return scope;
    }

    public boolean isWantsOpenSourceOnly() {
        return wantsOpenSourceOnly;
    }

    public DeploymentPreference getDeploymentPreference() {
        return deploymentPreference;
    }

    public BudgetTier getBudgetTier() {
        return budgetTier;
    }
}
