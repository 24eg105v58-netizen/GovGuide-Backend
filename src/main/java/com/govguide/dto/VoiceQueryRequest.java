package com.govguide.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class VoiceQueryRequest {

    @NotBlank(message = "Query text is required")
    private String query;

    private String language = "en"; // en, te, hi

    public VoiceQueryRequest() {
    }

    public VoiceQueryRequest(String query, String language) {
        this.query = query;
        this.language = language != null ? language : "en";
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
