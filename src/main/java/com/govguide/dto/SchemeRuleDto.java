package com.govguide.dto;

import java.math.BigDecimal;

public class SchemeRuleDto {

    private Long id;
    private Integer minimumAge;
    private Integer maximumAge;
    private BigDecimal minimumIncome;
    private BigDecimal maximumIncome;
    private String eligibleStates;
    private String eligibleOccupations;
    private String eligibleSocialCategories;
    private String eligibleEducationLevels;
    private String genderRequirement;
    private String disabilityStatus;
    private BigDecimal landholdingMaxAcres;
    private String additionalConditions;

    public SchemeRuleDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }

    public BigDecimal getMinimumIncome() {
        return minimumIncome;
    }

    public void setMinimumIncome(BigDecimal minimumIncome) {
        this.minimumIncome = minimumIncome;
    }

    public BigDecimal getMaximumIncome() {
        return maximumIncome;
    }

    public void setMaximumIncome(BigDecimal maximumIncome) {
        this.maximumIncome = maximumIncome;
    }

    public String getEligibleStates() {
        return eligibleStates;
    }

    public void setEligibleStates(String eligibleStates) {
        this.eligibleStates = eligibleStates;
    }

    public String getEligibleOccupations() {
        return eligibleOccupations;
    }

    public void setEligibleOccupations(String eligibleOccupations) {
        this.eligibleOccupations = eligibleOccupations;
    }

    public String getEligibleSocialCategories() {
        return eligibleSocialCategories;
    }

    public void setEligibleSocialCategories(String eligibleSocialCategories) {
        this.eligibleSocialCategories = eligibleSocialCategories;
    }

    public String getEligibleEducationLevels() {
        return eligibleEducationLevels;
    }

    public void setEligibleEducationLevels(String eligibleEducationLevels) {
        this.eligibleEducationLevels = eligibleEducationLevels;
    }

    public String getGenderRequirement() {
        return genderRequirement;
    }

    public void setGenderRequirement(String genderRequirement) {
        this.genderRequirement = genderRequirement;
    }

    public String getDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(String disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public BigDecimal getLandholdingMaxAcres() {
        return landholdingMaxAcres;
    }

    public void setLandholdingMaxAcres(BigDecimal landholdingMaxAcres) {
        this.landholdingMaxAcres = landholdingMaxAcres;
    }

    public String getAdditionalConditions() {
        return additionalConditions;
    }

    public void setAdditionalConditions(String additionalConditions) {
        this.additionalConditions = additionalConditions;
    }
}
