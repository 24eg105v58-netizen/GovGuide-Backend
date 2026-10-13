package com.govguide.dto;

import java.util.List;

public class AdminDashboardStatsDto {

    private long totalUsers;
    private long totalSchemes;
    private long centralSchemes;
    private long stateSchemes;
    private long verifiedSchemes;
    private long unverifiedSchemes;
    private List<SchemeDto> recentSchemes;

    public AdminDashboardStatsDto() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalSchemes() {
        return totalSchemes;
    }

    public void setTotalSchemes(long totalSchemes) {
        this.totalSchemes = totalSchemes;
    }

    public long getCentralSchemes() {
        return centralSchemes;
    }

    public void setCentralSchemes(long centralSchemes) {
        this.centralSchemes = centralSchemes;
    }

    public long getStateSchemes() {
        return stateSchemes;
    }

    public void setStateSchemes(long stateSchemes) {
        this.stateSchemes = stateSchemes;
    }

    public long getVerifiedSchemes() {
        return verifiedSchemes;
    }

    public void setVerifiedSchemes(long verifiedSchemes) {
        this.verifiedSchemes = verifiedSchemes;
    }

    public long getUnverifiedSchemes() {
        return unverifiedSchemes;
    }

    public void setUnverifiedSchemes(long unverifiedSchemes) {
        this.unverifiedSchemes = unverifiedSchemes;
    }

    public List<SchemeDto> getRecentSchemes() {
        return recentSchemes;
    }

    public void setRecentSchemes(List<SchemeDto> recentSchemes) {
        this.recentSchemes = recentSchemes;
    }
}
