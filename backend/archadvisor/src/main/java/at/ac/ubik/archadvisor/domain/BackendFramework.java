package at.ac.ubik.archadvisor.domain;

import at.ac.ubik.archadvisor.domain.enums.LicenseType;
import at.ac.ubik.archadvisor.domain.enums.ProgrammingLanguage;
import at.ac.ubik.archadvisor.domain.enums.RuntimeType;

import java.time.Instant;
import java.util.Set;

public class BackendFramework extends Technology {
    private ProgrammingLanguage programmingLanguage;
    private RuntimeType runtimeType;
    private boolean serverlessFriendly;

    protected BackendFramework(Long id, String name, String description, LicenseType license, Set<String> tags, String githubUrl, String documentation, Instant lastUpdated) {
        super(id, name, description, license, tags, githubUrl, documentation, lastUpdated);
    }

    public ProgrammingLanguage getProgrammingLanguage() {
        return programmingLanguage;
    }

}
