package com.bloodbank.bloodbank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_request")
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Convert(converter = BloodGroupConverter.class)
    @Column(nullable = false)
    private Donor.BloodGroup bloodGroup;

    @Column(nullable = false)
    private Integer unitsNeeded = 1;

    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgencyLevel = UrgencyLevel.NORMAL;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime fulfilledAt;

    public enum UrgencyLevel { NORMAL, URGENT, CRITICAL }
    public enum RequestStatus { PENDING, MATCHED, FULFILLED, CANCELLED }

    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public Donor.BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(Donor.BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public Integer getUnitsNeeded() { return unitsNeeded; }
    public void setUnitsNeeded(Integer unitsNeeded) { this.unitsNeeded = unitsNeeded; }

    public UrgencyLevel getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(UrgencyLevel urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getFulfilledAt() { return fulfilledAt; }
    public void setFulfilledAt(LocalDateTime fulfilledAt) { this.fulfilledAt = fulfilledAt; }
}