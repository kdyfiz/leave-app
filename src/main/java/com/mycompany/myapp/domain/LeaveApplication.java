package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.LeaveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Leave Application System JDL
 * Entities: LeaveApplication
 * Enum: LeaveStatus
 * Relationship: LeaveApplication ↔ User (applicant)
 */
@Schema(
    description = "Leave Application System JDL\nEntities: LeaveApplication\nEnum: LeaveStatus\nRelationship: LeaveApplication ↔ User (applicant)"
)
@Entity
@Table(name = "leave_application")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LeaveApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Size(min = 10, max = 500)
    @Column(name = "reason", length = 500, nullable = false)
    private String reason;

    @NotNull
    @Column(name = "submission_date", nullable = false)
    private Instant submissionDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status;

    @Size(max = 500)
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @ManyToOne(optional = false)
    @NotNull
    private User applicant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LeaveApplication id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LeaveApplication startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public LeaveApplication endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return this.reason;
    }

    public LeaveApplication reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getSubmissionDate() {
        return this.submissionDate;
    }

    public LeaveApplication submissionDate(Instant submissionDate) {
        this.setSubmissionDate(submissionDate);
        return this;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LeaveStatus getStatus() {
        return this.status;
    }

    public LeaveApplication status(LeaveStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return this.rejectionReason;
    }

    public LeaveApplication rejectionReason(String rejectionReason) {
        this.setRejectionReason(rejectionReason);
        return this;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public User getApplicant() {
        return this.applicant;
    }

    public void setApplicant(User user) {
        this.applicant = user;
    }

    public LeaveApplication applicant(User user) {
        this.setApplicant(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeaveApplication)) {
            return false;
        }
        return getId() != null && getId().equals(((LeaveApplication) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LeaveApplication{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", reason='" + getReason() + "'" +
            ", submissionDate='" + getSubmissionDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", rejectionReason='" + getRejectionReason() + "'" +
            "}";
    }
}
