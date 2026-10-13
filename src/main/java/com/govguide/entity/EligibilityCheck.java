package com.govguide.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eligibility_checks")
public class EligibilityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheme_id", nullable = false)
    private Scheme scheme;

    @Column(name = "eligibility_status", nullable = false, length = 30)
    private String eligibilityStatus; // LIKELY_ELIGIBLE, POSSIBLY_ELIGIBLE, NOT_ELIGIBLE

    @Column(name = "evaluated_conditions", columnDefinition = "TEXT")
    private String evaluatedConditions;

    @Column(name = "checked_at", updatable = false)
    private LocalDateTime checkedAt;

    public EligibilityCheck() {
    }

    public EligibilityCheck(User user, Scheme scheme, String eligibilityStatus, String evaluatedConditions) {
        this.user = user;
        this.scheme = scheme;
        this.eligibilityStatus = eligibilityStatus;
        this.evaluatedConditions = evaluatedConditions;
    }

    @PrePersist
    protected void onCreate() {
        this.checkedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public String getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(String eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public String getEvaluatedConditions() {
        return evaluatedConditions;
    }

    public void setEvaluatedConditions(String evaluatedConditions) {
        this.evaluatedConditions = evaluatedConditions;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}
