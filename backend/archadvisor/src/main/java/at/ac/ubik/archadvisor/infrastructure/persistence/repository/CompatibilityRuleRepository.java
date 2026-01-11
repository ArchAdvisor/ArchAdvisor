package at.ac.ubik.archadvisor.infrastructure.persistence.repository;

import at.ac.ubik.archadvisor.infrastructure.persistence.entity.CompatibilityRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRuleEntity, Long> {
}
