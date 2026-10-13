package com.govguide.dto;

import java.util.ArrayList;
import java.util.List;

public class VoiceQueryResponse {

    private String spokenResponse; // Text formatted specifically for Text-To-Speech playback in selected language
    private String displayResponse; // Text formatted for display in chat bubble
    private String intent;
    private String detectedLanguage;
    private List<SchemeDto> matchedSchemes = new ArrayList<>();
    private List<String> suggestedFollowUps = new ArrayList<>();

    public VoiceQueryResponse() {
    }

    public VoiceQueryResponse(String spokenResponse, String displayResponse, String intent, String detectedLanguage) {
        this.spokenResponse = spokenResponse;
        this.displayResponse = displayResponse;
        this.intent = intent;
        this.detectedLanguage = detectedLanguage;
    }

    public String getSpokenResponse() {
        return spokenResponse;
    }

    public void setSpokenResponse(String spokenResponse) {
        this.spokenResponse = spokenResponse;
    }

    public String getDisplayResponse() {
        return displayResponse;
    }

    public void setDisplayResponse(String displayResponse) {
        this.displayResponse = displayResponse;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public List<SchemeDto> getMatchedSchemes() {
        return matchedSchemes;
    }

    public void setMatchedSchemes(List<SchemeDto> matchedSchemes) {
        this.matchedSchemes = matchedSchemes;
    }

    public List<String> getSuggestedFollowUps() {
        return suggestedFollowUps;
    }

    public void setSuggestedFollowUps(List<String> suggestedFollowUps) {
        this.suggestedFollowUps = suggestedFollowUps;
    }
}
