package at.ac.ubik.archadvisor.domain;


import java.util.List;

public final class Recommendation {

    private final Technology technology;
    private final double score;
    private final List<String> warnings;

    public Recommendation(Technology technology, double score, List<String> warnings) {
        this.technology = technology;
        this.score = score;
        this.warnings = List.copyOf(warnings);
    }

    public Technology getTechnology() {
        return technology;
    }

    public double getScore() {
        return score;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}

