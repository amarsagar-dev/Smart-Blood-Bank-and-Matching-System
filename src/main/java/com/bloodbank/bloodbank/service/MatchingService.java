package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.Donor.BloodGroup;
import com.bloodbank.bloodbank.pattern.MatchingStrategy;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MatchingService {

    private final MatchingStrategy matchingStrategy;

    public MatchingService(MatchingStrategy matchingStrategy) {
        this.matchingStrategy = matchingStrategy;
    }

    public List<Donor> findMatchingDonors(BloodGroup bloodGroup) {
        return matchingStrategy.findMatchingDonors(bloodGroup);
    }
}