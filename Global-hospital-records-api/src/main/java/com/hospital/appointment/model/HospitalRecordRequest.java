package com.hospital.appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A patient's request to obtain their own records from a hospital. Separate
 * from {@link Appointment} because a records request isn't itself a visit —
 * it can be filed independently, or optionally scoped to one specific past
 * appointment (e.g. "the records from my March 3rd visit").
 */
@Entity
@Table(name = "hospital_record_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRecordRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Hospital is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    // Optional: narrows the request to one specific past visit rather than
    // "everything you have on file for me." Must belong to the same
    // patient and hospital as the request itself — enforced in the service
    // layer, not at the DB level.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    // Why the patient wants the records (e.g. "for insurance", "second
    // opinion"). Optional — patients generally have a right to their own
    // records regardless of stated reason; this is informational only.
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordRequestStatus status = RecordRequestStatus.PENDING;

    // Set when a hospital staff member approves, rejects, or fulfills the
    // request — e.g. a rejection reason, or delivery instructions on
    // fulfillment. Optional, free text.
    private String staffNotes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
