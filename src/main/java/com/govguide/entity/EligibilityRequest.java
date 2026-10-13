
package com.govguide.entity;

public class EligibilityRequest {

    private Integer age;
    private String state;
    private Double annualFamilyIncome;
    private String occupation;
    private String category;

    // null means the user has not answered
    private Boolean farmer;
    private Boolean ownsLand;
    private Boolean ownsHouse;
    private Boolean hasLpgConnection;

    private String educationLevel;

    public EligibilityRequest() {}

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

    public Double getAnnualFamilyIncome() {
        return annualFamilyIncome;
    }

    public void setAnnualFamilyIncome(Double annualFamilyIncome) {
        this.annualFamilyIncome = annualFamilyIncome;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean isFarmer() {
        return farmer;
    }

    public void setFarmer(Boolean farmer) {
        this.farmer = farmer;
    }

    public Boolean isOwnsLand() {
        return ownsLand;
    }

    public void setOwnsLand(Boolean ownsLand) {
        this.ownsLand = ownsLand;
    }

    public Boolean isOwnsHouse() {
        return ownsHouse;
    }

    public void setOwnsHouse(Boolean ownsHouse) {
        this.ownsHouse = ownsHouse;
    }

    public Boolean isHasLpgConnection() {
        return hasLpgConnection;
    }

    public void setHasLpgConnection(Boolean hasLpgConnection) {
        this.hasLpgConnection = hasLpgConnection;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }
}

