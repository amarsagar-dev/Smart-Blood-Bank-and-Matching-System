package com.bloodbank.bloodbank.repository;

import com.bloodbank.bloodbank.model.DonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationHistoryRepository extends JpaRepository<DonationHistory, Integer> {
    long countByDonorDonorId(Integer donorId);
    List<DonationHistory> findByDonorDonorIdOrderByDonationDateDesc(Integer donorId);
}