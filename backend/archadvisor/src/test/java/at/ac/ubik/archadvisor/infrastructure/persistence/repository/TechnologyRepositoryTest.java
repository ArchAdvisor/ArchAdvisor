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
        TechnologyEntity saved = repo.save(
                new TechnologyEntity(
                        "Spring Boot",
                        TechnologyKind.BACKEND,
                        RuntimeType.JDK,
                        ProgrammingLanguage.JAVA,
                        Instant.now(),
                        "backend,java,spring",
                        "https://github.com/spring-projects/spring-boot",
                        "https://docs.spring.io/spring-boot/",
                        LicenseType.APACHE_2_0)
        );


        List<TechnologyEntity> all = repo.findAll();

        assertFalse(all.isEmpty());
        assertEquals("Spring Boot", all.get(0).getName());
    }

}