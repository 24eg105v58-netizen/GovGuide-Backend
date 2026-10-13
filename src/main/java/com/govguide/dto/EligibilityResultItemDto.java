package com.govguide.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EligibilityResultItemDto {

    private Long schemeId;
    private String schemeName;
    private String schemeCode;
    private String category;
    private String governmentLevel;
    private String department;
    private String status; // LIKELY_ELIGIBLE, POSSIBLY_ELIGIBLE, NOT_ELIGIBLE
    private List<String> matchingConditions = new ArrayList<>();
    private List<String> unmetConditions = new ArrayList<>();
    private List<String> missingDetails = new ArrayList<>();
    private String benefits;
    private String officialUrl;
    private List<String> requiredDocuments = new ArrayList<>();
    private LocalDate lastVerifiedAt;
    private String informationSource;

    public EligibilityResultItemDto() {
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getMatchingConditions() {
        return matchingConditions;
    }

    public void setMatchingConditions(List<String> matchingConditions) {
        this.matchingConditions = matchingConditions;
    }

    public List<String> getUnmetConditions() {
        return unmetConditions;
    }

    public void setUnmetConditions(List<String> unmetConditions) {
        this.unmetConditions = unmetConditions;
    }

    public List<String> getMissingDetails() {
        return missingDetails;
    }

    public void setMissingDetails(List<String> missingDetails) {
        this.missingDetails = missingDetails;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getOfficialUrl() {
        return officialUrl;
    }

    public void setOfficialUrl(String officialUrl) {
        this.officialUrl = officialUrl;
    }

    public List<String> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<String> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
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
}
