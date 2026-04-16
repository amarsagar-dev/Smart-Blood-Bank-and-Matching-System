package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.model.*;
import com.bloodbank.bloodbank.model.BloodRequest.RequestStatus;
import com.bloodbank.bloodbank.model.DonorResponse.ResponseStatus;
import com.bloodbank.bloodbank.model.BloodRequest.UrgencyLevel;
import com.bloodbank.bloodbank.model.Donor.BloodGroup;
import com.bloodbank.bloodbank.pattern.BloodRequestSubject;
import com.bloodbank.bloodbank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BloodRequestService {

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private BloodRequestSubject bloodRequestSubject;

    @Autowired
    private DonorResponseRepository donorResponseRepository;

    @Autowired
    private DonationHistoryRepository donationHistoryRepository;

    public BloodRequest createRequest(Integer hospitalId, String bloodGroup,
                                       int units, String urgency) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        BloodGroup bg = BloodGroup.fromValue(bloodGroup);
        UrgencyLevel ul = UrgencyLevel.valueOf(urgency);

        BloodRequest request = new BloodRequest();
        request.setHospital(hospital);
        request.setBloodGroup(bg);
        request.setUnitsNeeded(units);
        request.setUrgencyLevel(ul);
        request.setStatus(RequestStatus.PENDING);

        BloodRequest saved = bloodRequestRepository.save(request);

        List<Donor> matchingDonors = matchingService.findMatchingDonors(bg);
        if (!matchingDonors.isEmpty()) {
            saved.setStatus(RequestStatus.MATCHED);
            bloodRequestRepository.save(saved);
            bloodRequestSubject.notifyObservers(matchingDonors, saved);
        }

        return saved;
    }

    public List<BloodRequest> getRequestsByHospital(Integer hospitalId) {
        return bloodRequestRepository.findByHospitalHospitalId(hospitalId);
    }

    public List<BloodRequest> getAllRequests() {
        return bloodRequestRepository.findAll();
    }

    public List<BloodRequest> getPendingRequests() {
        return bloodRequestRepository.findByStatus(RequestStatus.PENDING);
    }

    public Map<Integer, Long> getAcceptedDonorCounts(List<BloodRequest> requests) {
        Map<Integer, Long> acceptedCounts = new HashMap<>();
        for (BloodRequest request : requests) {
            long count = donorResponseRepository.countByBloodRequestRequestIdAndResponseStatus(
                    request.getRequestId(), ResponseStatus.ACCEPTED
            );
            acceptedCounts.put(request.getRequestId(), count);
        }
        return acceptedCounts;
    }

    public BloodRequest cancelRequest(Integer hospitalId, Integer requestId) {
        BloodRequest request = bloodRequestRepository
                .findByRequestIdAndHospitalHospitalId(requestId, hospitalId)
                .orElseThrow(() -> new RuntimeException("Request not found for this hospital"));

        if (request.getStatus() == RequestStatus.CANCELLED) {
            return request;
        }
        if (request.getStatus() == RequestStatus.FULFILLED) {
            throw new RuntimeException("Fulfilled request cannot be cancelled");
        }

        request.setStatus(RequestStatus.CANCELLED);
        request.setFulfilledAt(null);
        return bloodRequestRepository.save(request);
    }

    public BloodRequest fulfillRequest(Integer hospitalId, Integer requestId) {
        BloodRequest request = bloodRequestRepository
                .findByRequestIdAndHospitalHospitalId(requestId, hospitalId)
                .orElseThrow(() -> new RuntimeException("Request not found for this hospital"));

        if (request.getStatus() == RequestStatus.FULFILLED) {
            return request;
        }
        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new RuntimeException("Cancelled request cannot be fulfilled");
        }

        List<DonorResponse> acceptedResponses = donorResponseRepository
                .findByBloodRequestRequestIdAndResponseStatus(requestId, ResponseStatus.ACCEPTED);
        if (acceptedResponses.isEmpty()) {
            throw new RuntimeException("No donor has accepted this request yet");
        }

        int unitsPerDonor = Math.max(1, (int) Math.ceil((double) request.getUnitsNeeded() / acceptedResponses.size()));
        for (DonorResponse donorResponse : acceptedResponses) {
            DonationHistory history = new DonationHistory();
            history.setDonor(donorResponse.getDonor());
            history.setBloodRequest(request);
            history.setDonationDate(LocalDate.now());
            history.setUnitsDonated(unitsPerDonor);
            history.setNotes("Donation recorded from accepted emergency request #" + request.getRequestId());
            donationHistoryRepository.save(history);
        }

        request.setStatus(RequestStatus.FULFILLED);
        request.setFulfilledAt(LocalDateTime.now());
        return bloodRequestRepository.save(request);
    }
}