package at.ac.ubik.archadvisor.infrastructure.persistence.entity;

import at.ac.ubik.archadvisor.domain.enums.RuleLevel;
import jakarta.persistence.*;

@Entity
@Table(
        name = "compatibility_rule",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_compatibility_rule_tech_pair",
                        columnNames = {"source_technology_id", "target_technology_id"}
                )
        }
)
public class CompatibilityRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Always the "smaller" technology by ID (canonical ordering).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_technology_id", nullable = false)
    private TechnologyEntity sourceTechnology;

    /**
     * Always the "larger" technology by ID (canonical ordering).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_technology_id", nullable = false)
    private TechnologyEntity targetTechnology;

    @Column(name = "message", length = 512)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private RuleLevel level;

    protected CompatibilityRuleEntity() {

    }

    private CompatibilityRuleEntity(
            TechnologyEntity sourceTechnology,
            TechnologyEntity targetTechnology,
            String message,
            RuleLevel level
    ) {
        this.sourceTechnology = sourceTechnology;
        this.targetTechnology = targetTechnology;
        this.message = message;
        this.level = level;
    }

    /**
     * Factory method that enforces symmetric canonical ordering:
     * the technology with the smaller ID becomes 'sourceTechnology',
     * the one with the larger ID becomes 'targetTechnology'.
     */
    public static CompatibilityRuleEntity of(
            TechnologyEntity technology1,
            TechnologyEntity technology2,
            String message,
            RuleLevel level
    ) {
        if (technology1 == null || technology2 == null) {
            throw new IllegalArgumentException("Technologies must not be null");
        }
        if (technology1.getId() == null || technology2.getId() == null) {
            throw new IllegalStateException(
                    "Technologies must be persisted (non-null ID) before creating a compatibility rule"
            );
        }

        TechnologyEntity low;
        TechnologyEntity high;

        if (technology1.getId() < technology2.getId()) {
            low = technology1;
            high = technology2;
        } else if (technology1.getId() > technology2.getId()) {
            low = technology2;
            high = technology1;
        } else {
            throw new IllegalArgumentException(
                    "Cannot create a compatibility rule for the same technology ID: " + technology1.getId()
            );
        }

        return new CompatibilityRuleEntity(low, high, message, level);
    }

    public Long getId() {
        return id;
    }

    public TechnologyEntity getSourceTechnology() {
        return sourceTechnology;
    }

    public TechnologyEntity getTargetTechnology() {
        return targetTechnology;
    }

    public String getMessage() {
        return message;
    }

    public RuleLevel getLevel() {
        return level;
    }


}

