package com.govguide.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SchemeDto {

    private Long id;
    private String schemeName;
    private String schemeCode;
    private String category;
    private String governmentLevel;
    private String department;
    private String shortDescription;
    private String detailedDescription;
    private String whyExists;
    private String whoCanBenefit;
    private String benefits;
    private String applicationProcess;
    private String officialUrl;
    private String verificationStatus;
    private String publicationStatus;
    private String applicationStatus;
    private LocalDate lastVerifiedAt;
    private String informationSource;
    private SchemeRuleDto eligibilityRule;
    private List<DocumentDto> documents = new ArrayList<>();
    private boolean isSaved;

    public SchemeDto() {
    }

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

    public SchemeRuleDto getEligibilityRule() {
        return eligibilityRule;
    }

    public void setEligibilityRule(SchemeRuleDto eligibilityRule) {
        this.eligibilityRule = eligibilityRule;
    }

    public List<DocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDto> documents) {
        this.documents = documents;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}
