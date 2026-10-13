package com.govguide.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class EligibilityCheckRequest {

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1 year")
    @Max(value = 120, message = "Please enter a valid age up to 120")
    private Integer age;

    @NotBlank(message = "State/UT is required")
    private String state;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @NotNull(message = "Annual family income is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Annual family income cannot be negative")
    private BigDecimal annualIncome;

    @NotBlank(message = "Social category is required")
    private String socialCategory; // General, SC, ST, OBC, Prefer not to disclose

    @NotBlank(message = "Education level is required")
    private String educationLevel;

    // Optional fields
    private String gender = "ALL"; // ALL, FEMALE, MALE, TRANSGENDER
    private String disabilityStatus = "NOT_REQUIRED"; // ALL, REQUIRED, NOT_REQUIRED
    private BigDecimal landholdingAcres; // Optional for farmers

    public EligibilityCheckRequest() {
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getSocialCategory() {
        return socialCategory;
    }

    public void setSocialCategory(String socialCategory) {
        this.socialCategory = socialCategory;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(String disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public BigDecimal getLandholdingAcres() {
        return landholdingAcres;
    }

    public void setLandholdingAcres(BigDecimal landholdingAcres) {
        this.landholdingAcres = landholdingAcres;
    }
}
