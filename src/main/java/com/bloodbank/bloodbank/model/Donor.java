package com.bloodbank.bloodbank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;

@Entity
@Table(name = "donor")
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer donorId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Convert(converter = BloodGroupConverter.class)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    private LocalDate lastDonationDate;
    private Double latitude;
    private Double longitude;

    public enum BloodGroup {
        A_POS("A+"),
        A_NEG("A-"),
        B_POS("B+"),
        B_NEG("B-"),
        AB_POS("AB+"),
        AB_NEG("AB-"),
        O_POS("O+"),
        O_NEG("O-");

        private final String dbValue;

        BloodGroup(String dbValue) {
            this.dbValue = dbValue;
        }

        public String getDbValue() {
            return dbValue;
        }

        public static BloodGroup fromValue(String value) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Blood group is required");
            }

            String normalized = value.trim();
            return Arrays.stream(values())
                    .filter(bg -> bg.name().equalsIgnoreCase(normalized)
                            || bg.dbValue.equalsIgnoreCase(normalized))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid blood group: " + value));
        }
    }
    public enum AvailabilityStatus { AVAILABLE, UNAVAILABLE, COOLDOWN }

    public Integer getDonorId() { return donorId; }
    public void setDonorId(Integer donorId) { this.donorId = donorId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}