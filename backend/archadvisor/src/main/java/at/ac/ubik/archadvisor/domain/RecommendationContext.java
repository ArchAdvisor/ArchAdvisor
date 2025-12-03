package at.ac.ubik.archadvisor.domain;

public final class RecommendationContext {
    private final TechnicalProfile technicalProfile;
    private final TeamProfile teamProfile;
    private final PriorityRanking priorityRanking;

    public RecommendationContext(TechnicalProfile questionnaireResult,
                                 TeamProfile teamProfile,
                                 PriorityRanking priorityRanking) {
        this.technicalProfile = questionnaireResult;
        this.teamProfile = teamProfile;
        this.priorityRanking = priorityRanking;
    }

    public TechnicalProfile getTechnicalProfile() {
        return technicalProfile;
    }

    public TeamProfile getTeamProfile() {
        return teamProfile;
    }

    public PriorityRanking getPriorityRanking() {
        return priorityRanking;
    }

    @Override
    public String toString() {
        return "RecommendationContext{" +
                "technicalProfile=" + technicalProfile +
                ", teamProfile=" + teamProfile +
                ", priorityRanking=" + priorityRanking +
                '}';
    }
}

