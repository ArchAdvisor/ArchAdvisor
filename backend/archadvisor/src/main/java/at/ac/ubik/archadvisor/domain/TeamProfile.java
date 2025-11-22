package at.ac.ubik.archadvisor.domain;

import at.ac.ubik.archadvisor.domain.enums.ProgrammingLanguage;

import java.util.Set;

public final class TeamProfile {
    private final int teamSize;
    private final String experienceLevel;
    private final Set<ProgrammingLanguage> familiarLanguages;

    public TeamProfile(int teamSize, String experienceLevel, Set<ProgrammingLanguage> familiarLanguages) {
        this.teamSize = teamSize;
        this.experienceLevel = experienceLevel;
        this.familiarLanguages = familiarLanguages;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public Set<ProgrammingLanguage> getFamiliarLanguages() {
        return familiarLanguages;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }
}
