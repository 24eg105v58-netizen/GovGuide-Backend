package com.govguide.dto;

import java.util.ArrayList;
import java.util.List;

public class EligibilityCheckResponse {

    private List<EligibilityResultItemDto> likelyEligible = new ArrayList<>();
    private List<EligibilityResultItemDto> possiblyEligible = new ArrayList<>();
    private List<EligibilityResultItemDto> notEligible = new ArrayList<>();
    private int totalEvaluated;
    private String disclaimer = "Eligibility results are preliminary guidance based on the available scheme information. Final eligibility is determined by the relevant government department.";

    public EligibilityCheckResponse() {
    }

    public List<EligibilityResultItemDto> getLikelyEligible() {
        return likelyEligible;
    }

    public void setLikelyEligible(List<EligibilityResultItemDto> likelyEligible) {
        this.likelyEligible = likelyEligible;
    }

    public List<EligibilityResultItemDto> getPossiblyEligible() {
        return possiblyEligible;
    }

    public void setPossiblyEligible(List<EligibilityResultItemDto> possiblyEligible) {
        this.possiblyEligible = possiblyEligible;
    }

    public List<EligibilityResultItemDto> getNotEligible() {
        return notEligible;
    }

    public void setNotEligible(List<EligibilityResultItemDto> notEligible) {
        this.notEligible = notEligible;
    }

    public int getTotalEvaluated() {
        return totalEvaluated;
    }

    public void setTotalEvaluated(int totalEvaluated) {
        this.totalEvaluated = totalEvaluated;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
