package com.govguide.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "scheme_documents")
public class SchemeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheme_id", nullable = false)
    @JsonBackReference
    private Scheme scheme;

    @Column(name = "document_name", nullable = false, length = 150)
    private String documentName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "mandatory_status", nullable = false)
    private Boolean mandatoryStatus = true;

    public SchemeDocument() {
    }

    public SchemeDocument(String documentName, String description, Boolean mandatoryStatus) {
        this.documentName = documentName;
        this.description = description;
        this.mandatoryStatus = mandatoryStatus != null ? mandatoryStatus : true;
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
