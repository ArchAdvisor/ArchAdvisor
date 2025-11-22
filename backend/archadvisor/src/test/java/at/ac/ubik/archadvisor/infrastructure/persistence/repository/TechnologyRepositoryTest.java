package at.ac.ubik.archadvisor.infrastructure.persistence.repository;

import at.ac.ubik.archadvisor.domain.enums.LicenseType;
import at.ac.ubik.archadvisor.domain.enums.ProgrammingLanguage;
import at.ac.ubik.archadvisor.domain.enums.RuntimeType;
import at.ac.ubik.archadvisor.domain.enums.TechnologyKind;
import at.ac.ubik.archadvisor.infrastructure.persistence.entity.TechnologyEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
class TechnologyRepositoryTest {

    @Autowired
    private TechnologyRepository repo;

    @Test
    void shouldSaveAndLoadTechnology() {
        TechnologyEntity backendEntity =
                new TechnologyEntity(
                        "Spring Boot",
                        TechnologyKind.BACKEND,
                        Instant.now(),
                        "backend,java,spring",
                        "https://github.com/spring-projects/spring-boot",
                        "https://docs.spring.io/spring-boot/",
                        LicenseType.APACHE_2_0);
        backendEntity.setRuntime(RuntimeType.JDK);
        backendEntity.setLanguage(ProgrammingLanguage.JAVA);
        TechnologyEntity savedBackEnd = repo.save(backendEntity);
        List<TechnologyEntity> all = repo.findAll();
        assertFalse(all.isEmpty());
        assertEquals("Spring Boot", all.getFirst().getName());
        assertEquals(ProgrammingLanguage.JAVA, all.getFirst().getLanguage());
    }

}