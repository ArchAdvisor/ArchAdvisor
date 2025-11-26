package at.ac.ubik.archadvisor.domain;

import at.ac.ubik.archadvisor.domain.enums.RuleLevel;

public class CompatibilityRule {
    private Technology sourceTechnology;
    private Technology targetTechnology;
    private String message;
    private RuleLevel level;

    private CompatibilityRule(Technology source, Technology target, String message, RuleLevel level) {
        this.sourceTechnology = source;
        this.targetTechnology = target;
        this.message = message;
        this.level = level;
    }

    public static CompatibilityRule of(Technology t1, Technology t2, String message, RuleLevel level) {
        Technology low = t1.getId() < t2.getId() ? t1 : t2;
        Technology high = t1.getId() < t2.getId() ? t2 : t1;

        return new CompatibilityRule(low, high, message, level);
    }
}
