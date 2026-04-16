package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.Donor.BloodGroup;
import com.bloodbank.bloodbank.model.Donor.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Integer> {
    List<Donor> findByBloodGroupAndAvailabilityStatus(BloodGroup bloodGroup, AvailabilityStatus status);
    Optional<Donor> findByUserUserId(Integer userId);
}