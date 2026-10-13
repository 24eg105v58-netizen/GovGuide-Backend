package com.govguide.dto;

import java.math.BigDecimal;

public class DocumentDto {

    private Long id;
    private String documentName;
    private String description;
    private Boolean mandatoryStatus;

    public DocumentDto() {
    }

    public DocumentDto(Long id, String documentName, String description, Boolean mandatoryStatus) {
        this.id = id;
        this.documentName = documentName;
        this.description = description;
        this.mandatoryStatus = mandatoryStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getMandatoryStatus() {
        return mandatoryStatus;
    }

    public void setMandatoryStatus(Boolean mandatoryStatus) {
        this.mandatoryStatus = mandatoryStatus;
    }
}
