package com.govguide.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "scheme_eligibility_rules")
public class SchemeEligibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheme_id", nullable = false)
    @JsonBackReference
    private Scheme scheme;

    @Column(name = "minimum_age")
    private Integer minimumAge = 0;

    @Column(name = "maximum_age")
    private Integer maximumAge = 120;

    @Column(name = "minimum_income", precision = 12, scale = 2)
    private BigDecimal minimumIncome = BigDecimal.ZERO;

    @Column(name = "maximum_income", precision = 12, scale = 2)
    private BigDecimal maximumIncome;

    @Column(name = "eligible_states", columnDefinition = "TEXT")
    private String eligibleStates = "ALL";

    @Column(name = "eligible_occupations", columnDefinition = "TEXT")
    private String eligibleOccupations = "ALL";

    @Column(name = "eligible_social_categories", length = 255)
    private String eligibleSocialCategories = "ALL";

    @Column(name = "eligible_education_levels", length = 255)
    private String eligibleEducationLevels = "ALL";

    @Column(name = "gender_requirement", length = 20)
    private String genderRequirement = "ALL"; // ALL, FEMALE, MALE, TRANSGENDER

    @Column(name = "disability_status", length = 20)
    private String disabilityStatus = "ALL"; // ALL, REQUIRED, NOT_REQUIRED

    @Column(name = "landholding_max_acres", precision = 5, scale = 2)
    private BigDecimal landholdingMaxAcres;

    @Column(name = "additional_conditions", columnDefinition = "TEXT")
    private String additionalConditions;

    public SchemeEligibilityRule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
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
