package com.bloodbank.bloodbank.model;
import jakarta.persistence.*;
// import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "donation_history")
// @Data
public class DonationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer donationId;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private BloodRequest bloodRequest;

    @Column(nullable = false)
    private LocalDate donationDate;

    @Column(nullable = false)
    private Integer unitsDonated = 1;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Integer getDonationId() { return donationId; }
    public void setDonationId(Integer donationId) { this.donationId = donationId; }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }

    public BloodRequest getBloodRequest() { return bloodRequest; }
    public void setBloodRequest(BloodRequest bloodRequest) { this.bloodRequest = bloodRequest; }

    public LocalDate getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }

    public Integer getUnitsDonated() { return unitsDonated; }
    public void setUnitsDonated(Integer unitsDonated) { this.unitsDonated = unitsDonated; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}