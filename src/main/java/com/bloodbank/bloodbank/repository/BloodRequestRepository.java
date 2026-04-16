package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.model.BloodRequest;
import com.bloodbank.bloodbank.model.BloodRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Integer> {
    List<BloodRequest> findByStatus(RequestStatus status);
    List<BloodRequest> findByHospitalHospitalId(Integer hospitalId);
    Optional<BloodRequest> findByRequestIdAndHospitalHospitalId(Integer requestId, Integer hospitalId);
    long countByStatus(RequestStatus status);
    long countByHospitalHospitalId(Integer hospitalId);
    long countByHospitalHospitalIdAndStatus(Integer hospitalId, RequestStatus status);
}