package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.model.DonorResponse;
import com.bloodbank.bloodbank.model.DonorResponse.ResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonorResponseRepository extends JpaRepository<DonorResponse, Integer> {
    List<DonorResponse> findByDonorDonorIdOrderByBloodRequestRequestedAtDesc(Integer donorId);
    Optional<DonorResponse> findByBloodRequestRequestIdAndDonorDonorId(Integer requestId, Integer donorId);
    List<DonorResponse> findByBloodRequestRequestIdAndResponseStatus(Integer requestId, ResponseStatus responseStatus);
    long countByBloodRequestRequestIdAndResponseStatus(Integer requestId, ResponseStatus responseStatus);
}
