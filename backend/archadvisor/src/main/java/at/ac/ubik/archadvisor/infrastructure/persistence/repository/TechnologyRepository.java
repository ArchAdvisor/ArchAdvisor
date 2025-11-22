package at.ac.ubik.archadvisor.infrastructure.persistence.repository;

import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnologyRepository extends JpaRepository<TechnologyEntity, Long> {
}
