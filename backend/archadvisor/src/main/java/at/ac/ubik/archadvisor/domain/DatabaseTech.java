package at.ac.ubik.archadvisor.domain;

import at.ac.ubik.archadvisor.domain.enums.LicenseType;

import java.time.Instant;
import java.util.Set;

public class DatabaseTech extends Technology {

    protected DatabaseTech(Long id, String name, String description, LicenseType license, Set<String> tags, String githubUrl, String documentation, Instant lastUpdated) {
        super(id, name, description, license, tags, githubUrl, documentation, lastUpdated);
    }
}