package at.ac.ubik.archadvisor.infrastructure.persistence.entity;

import at.ac.ubik.archadvisor.domain.enums.*;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "technologies")
public class TechnologyEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private LicenseType license;
    @Enumerated(EnumType.STRING)
    private TechnologyKind kind;
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;
    @Enumerated(EnumType.STRING)
    private RuntimeType runtime;
    @Enumerated(EnumType.STRING)
    private DbModelType dbModelType;
    @Enumerated(EnumType.STRING)
    private MobilePlatform mobilePlatform;
    private String tagsCsv;
    private String githubUrl;
    private String documentationUrl;
    private String description;
    private Instant lastUpdated;
    private Boolean serverlessFriendly;
    @Column(name = "supports_ssr")
    private Boolean supportsSSR;
    private Double performanceScore;
    private Double scalabilityScore;
    private Double maintainabilityScore;
    private Double securityScore;
    private Double costEffectivenessScore;
    private Double communitySupportScore;
    private Double ecosystemMaturityScore;
    private Double vendorLockinScore;

    protected TechnologyEntity() {
    }

    public boolean isSupportsSSR() {
        return supportsSSR;
    }

    public void setSupportsSSR(boolean supportsSSR) {
        this.supportsSSR = supportsSSR;
    }

    /**
     * General base constructor for all technology types
     **/
    public TechnologyEntity(String name, TechnologyKind kind, Instant lastUpdated, String documentationUrl, String githubUrl, String tagsCsv, LicenseType license) {
        this.lastUpdated = lastUpdated;
        this.documentationUrl = documentationUrl;
        this.githubUrl = githubUrl;
        this.tagsCsv = tagsCsv;
        this.kind = kind;
        this.license = license;
        this.name = name;
    }

    public LicenseType getLicense() {
        return license;
    }

    public TechnologyKind getKind() {
        return kind;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public RuntimeType getRuntime() {
        return runtime;
    }

    public void setRuntime(RuntimeType runtime) {
        this.runtime = runtime;
    }

    public DbModelType getDbModelType() {
        return dbModelType;
    }

    public void setDbModelType(DbModelType dbModelType) {
        this.dbModelType = dbModelType;
    }

    public String getTagsCsv() {
        return tagsCsv;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public double getVendorLockinScore() {
        return vendorLockinScore;
    }

    public void setVendorLockinScore(double vendorLockinScore) {
        this.vendorLockinScore = vendorLockinScore;
    }

    public double getEcosystemMaturityScore() {
        return ecosystemMaturityScore;
    }

    public void setEcosystemMaturityScore(double ecosystemMaturityScore) {
        this.ecosystemMaturityScore = ecosystemMaturityScore;
    }

    public double getCommunitySupportScore() {
        return communitySupportScore;
    }

    public void setCommunitySupportScore(double communitySupportScore) {
        this.communitySupportScore = communitySupportScore;
    }

    public double getCostEffectivenessScore() {
        return costEffectivenessScore;
    }

    public void setCostEffectivenessScore(double costEffectivenessScore) {
        this.costEffectivenessScore = costEffectivenessScore;
    }

    public double getSecurityScore() {
        return securityScore;
    }

    public void setSecurityScore(double securityScore) {
        this.securityScore = securityScore;
    }

    public double getMaintainabilityScore() {
        return maintainabilityScore;
    }

    public void setMaintainabilityScore(double maintainabilityScore) {
        this.maintainabilityScore = maintainabilityScore;
    }

    public double getScalabilityScore() {
        return scalabilityScore;
    }

    public void setScalabilityScore(double scalabilityScore) {
        this.scalabilityScore = scalabilityScore;
    }

    public double getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(double performanceScore) {
        this.performanceScore = performanceScore;
    }

    public boolean isServerlessFriendly() {
        return serverlessFriendly;
    }

    public void setServerlessFriendly(boolean serverlessFriendly) {
        this.serverlessFriendly = serverlessFriendly;
    }

    public void setMobilePlatform(MobilePlatform mobilePlatform) {
        this.mobilePlatform = mobilePlatform;
    }

    public String getName() {
        return name;
    }

    public MobilePlatform getMobilePlatform() {
        return mobilePlatform;
    }
}

