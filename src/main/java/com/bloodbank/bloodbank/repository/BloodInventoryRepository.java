package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.model.BloodInventory;
import com.bloodbank.bloodbank.model.Donor.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Integer> {
    List<BloodInventory> findByHospitalHospitalId(Integer hospitalId);
    List<BloodInventory> findByBloodGroup(BloodGroup bloodGroup);
    Optional<BloodInventory> findByHospitalHospitalIdAndBloodGroup(Integer hospitalId, BloodGroup bloodGroup);
}