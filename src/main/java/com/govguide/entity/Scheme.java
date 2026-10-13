package com.govguide.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schemes")
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false, length = 255)
    private String schemeName;

    @Column(name = "scheme_code", nullable = false, unique = true, length = 50)
    private String schemeCode;

    @Column(nullable = false, length = 100)
    private String category; // Agriculture, Healthcare, Housing, Education, etc.

    @Column(name = "government_level", nullable = false, length = 20)
    private String governmentLevel; // CENTRAL or STATE

    @Column(nullable = false, length = 255)
    private String department;

    @Column(name = "short_description", nullable = false, columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "detailed_description", nullable = false, columnDefinition = "LONGTEXT")
    private String detailedDescription;

    @Column(name = "why_exists", columnDefinition = "TEXT")
    private String whyExists;

    @Column(name = "who_can_benefit", columnDefinition = "TEXT")
    private String whoCanBenefit;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "application_process", nullable = false, columnDefinition = "LONGTEXT")
    private String applicationProcess;

    @Column(name = "official_url", nullable = false, length = 500)
    private String officialUrl;

    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus = "VERIFIED"; // VERIFIED, UNVERIFIED, UNDER_REVIEW

    @Column(name = "publication_status", nullable = false, length = 20)
    private String publicationStatus = "PUBLISHED"; // PUBLISHED, DRAFT, ARCHIVED

    @Column(name = "application_status", nullable = false, length = 20)
    private String applicationStatus = "OPEN"; // OPEN, CLOSED, UNVERIFIED

    @Column(name = "last_verified_at", nullable = false)
    private LocalDate lastVerifiedAt;

    @Column(name = "information_source", nullable = false, length = 255)
    private String informationSource;

    @OneToOne(mappedBy = "scheme", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private SchemeEligibilityRule eligibilityRule;

    @OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<SchemeDocument> documents = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Scheme() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.lastVerifiedAt == null) {
            this.lastVerifiedAt = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGovernmentLevel() {
        return governmentLevel;
    }

    public void setGovernmentLevel(String governmentLevel) {
        this.governmentLevel = governmentLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getWhyExists() {
        return whyExists;
    }

    public void setWhyExists(String whyExists) {
        this.whyExists = whyExists;
    }

    public String getWhoCanBenefit() {
        return whoCanBenefit;
    }

    public void setWhoCanBenefit(String whoCanBenefit) {
        this.whoCanBenefit = whoCanBenefit;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getApplicationProcess() {
        return applicationProcess;
    }

    public void setApplicationProcess(String applicationProcess) {
        this.applicationProcess = applicationProcess;
    }

    public String getOfficialUrl() {
        return officialUrl;
    }

    public void setOfficialUrl(String officialUrl) {
        this.officialUrl = officialUrl;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getPublicationStatus() {
        return publicationStatus;
    }

    public void setPublicationStatus(String publicationStatus) {
        this.publicationStatus = publicationStatus;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public LocalDate getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(LocalDate lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public String getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(String informationSource) {
        this.informationSource = informationSource;
    }

    public SchemeEligibilityRule getEligibilityRule() {
        return eligibilityRule;
    }

    public void setEligibilityRule(SchemeEligibilityRule eligibilityRule) {
        this.eligibilityRule = eligibilityRule;
        if (eligibilityRule != null) {
            eligibilityRule.setScheme(this);
        }
    }

    public List<SchemeDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<SchemeDocument> documents) {
        this.documents = documents;
    }

    public void addDocument(SchemeDocument doc) {
        this.documents.add(doc);
        doc.setScheme(this);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
