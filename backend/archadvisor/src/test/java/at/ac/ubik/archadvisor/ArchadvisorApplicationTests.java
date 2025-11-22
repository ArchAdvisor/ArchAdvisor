package at.ac.ubik.archadvisor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ArchadvisorApplicationTests {

    @Test
    void smokeTest() {
        assert (1 + 1 == 2);
    }

}
