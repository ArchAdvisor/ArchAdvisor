package at.ac.ubik.archadvisor.domain;

import at.ac.ubik.archadvisor.domain.enums.DbModelType;
import at.ac.ubik.archadvisor.domain.enums.LicenseType;

import java.time.Instant;
import java.util.Set;

public class DatabaseTech extends Technology {
    private DbModelType dbModelType;

    public DatabaseTech(Long id, String name, String description, LicenseType license, Set<String> tags, String githubUrl, String documentation, Instant lastUpdated, DbModelType dbModelType) {
        super(id, name, description, license, tags, githubUrl, documentation, lastUpdated);
        this.dbModelType = dbModelType;
    }
}