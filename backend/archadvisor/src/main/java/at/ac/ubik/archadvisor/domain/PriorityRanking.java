package at.ac.ubik.archadvisor.domain;

import at.ac.ubik.archadvisor.domain.enums.PriorityAspect;

import java.util.HashMap;
import java.util.Map;

public final class PriorityRanking {
    //Aspect, ranking from 1 to N
    //e.g. (Performance, 1)
    private final HashMap<PriorityAspect, Integer> rankedPriorityAspects;
    private HashMap<PriorityAspect, Double> weightOfPriorityAspects;

    public PriorityRanking(HashMap<PriorityAspect, Integer> rankByAspect) {
        this.rankedPriorityAspects = rankByAspect;
        this.weightOfPriorityAspects = new HashMap<>();
    }

    public PriorityRanking() {
        this.rankedPriorityAspects = new HashMap<>();

    }

    public Map<PriorityAspect, Integer> getRankByAspect() {
        return rankedPriorityAspects;
    }

    public Map<PriorityAspect, Double> getWeightOfPriorityAspects() {
        return weightOfPriorityAspects;
    }

    public void setWeightOfPriorityAspects(PriorityAspect aspect, double weight) {
        this.weightOfPriorityAspects.put(aspect, weight);
    }

    public void setRanksOfPriorityAspects(PriorityAspect aspect, int rank) {
        this.rankedPriorityAspects.put(aspect, rank);
    }

    @Override
    public String toString() {
        return "PriorityRanking{" +
                "rankedPriorityAspects=" + rankedPriorityAspects;
    }
}

