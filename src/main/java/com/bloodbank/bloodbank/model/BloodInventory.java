package com.bloodbank.bloodbank.model;
import jakarta.persistence.*;
// import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_inventory")
// @Data
public class BloodInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inventoryId;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Convert(converter = BloodGroupConverter.class)
    @Column(nullable = false)
    private Donor.BloodGroup bloodGroup;

    @Column(nullable = false)
    private Integer unitsAvailable = 0;

    private LocalDate expiryDate;
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getInventoryId() { return inventoryId; }
    public void setInventoryId(Integer inventoryId) { this.inventoryId = inventoryId; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public Donor.BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(Donor.BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public Integer getUnitsAvailable() { return unitsAvailable; }
    public void setUnitsAvailable(Integer unitsAvailable) { this.unitsAvailable = unitsAvailable; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}