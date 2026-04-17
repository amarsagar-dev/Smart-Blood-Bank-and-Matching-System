package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.Donor.BloodGroup;
import com.bloodbank.bloodbank.model.Donor.AvailabilityStatus;
import com.bloodbank.bloodbank.model.User;
import com.bloodbank.bloodbank.repository.DonorRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DonorService {

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private UserRepository userRepository;

    public Donor saveDonor(User user, String bloodGroup, String city, String phone) {
        Donor donor = donorRepository
                .findByUserUserId(user.getUserId())
                .orElse(new Donor());

        donor.setUser(user);
        donor.setBloodGroup(BloodGroup.fromValue(bloodGroup));
        donor.setAvailabilityStatus(Donor.AvailabilityStatus.AVAILABLE);

        user.setCity(city);
        user.setPhone(phone);
        userRepository.save(user);

        return donorRepository.save(donor);
    }

    public Donor updateAvailabilityStatus(Integer userId, String status) {
        Donor donor = donorRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));

        donor.setAvailabilityStatus(AvailabilityStatus.valueOf(status));
        return donorRepository.save(donor);
    }

    public boolean hasProfile(Integer userId) {
        return donorRepository.findByUserUserId(userId).isPresent();
    }
}