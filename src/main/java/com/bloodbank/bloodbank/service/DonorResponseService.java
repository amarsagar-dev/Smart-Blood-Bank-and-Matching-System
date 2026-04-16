package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.model.BloodRequest;
import com.bloodbank.bloodbank.model.BloodRequest.RequestStatus;
import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.DonorResponse;
import com.bloodbank.bloodbank.model.DonorResponse.ResponseStatus;
import com.bloodbank.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.bloodbank.repository.DonorRepository;
import com.bloodbank.bloodbank.repository.DonorResponseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonorResponseService {

    private final DonorRepository donorRepository;
    private final DonorResponseRepository donorResponseRepository;
    private final BloodRequestRepository bloodRequestRepository;

    public DonorResponseService(
            DonorRepository donorRepository,
            DonorResponseRepository donorResponseRepository,
            BloodRequestRepository bloodRequestRepository) {
        this.donorRepository = donorRepository;
        this.donorResponseRepository = donorResponseRepository;
        this.bloodRequestRepository = bloodRequestRepository;
    }

    public List<DonorResponse> getResponsesForDonor(Integer userId) {
        Donor donor = donorRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));

        return donorResponseRepository.findByDonorDonorIdOrderByBloodRequestRequestedAtDesc(donor.getDonorId());
    }

    public DonorResponse acceptRequest(Integer userId, Integer requestId) {
        DonorResponse response = getDonorResponseForRequest(userId, requestId);
        BloodRequest request = response.getBloodRequest();

        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new RuntimeException("Request already cancelled");
        }
        if (request.getStatus() == RequestStatus.FULFILLED) {
            throw new RuntimeException("Request already fulfilled");
        }

        response.setResponseStatus(ResponseStatus.ACCEPTED);
        response.setRespondedAt(LocalDateTime.now());
        DonorResponse saved = donorResponseRepository.save(response);

        if (request.getStatus() == RequestStatus.PENDING) {
            request.setStatus(RequestStatus.MATCHED);
            bloodRequestRepository.save(request);
        }

        return saved;
    }

    public DonorResponse declineRequest(Integer userId, Integer requestId) {
        DonorResponse response = getDonorResponseForRequest(userId, requestId);
        BloodRequest request = response.getBloodRequest();

        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new RuntimeException("Request already cancelled");
        }
        if (request.getStatus() == RequestStatus.FULFILLED) {
            throw new RuntimeException("Request already fulfilled");
        }

        response.setResponseStatus(ResponseStatus.DECLINED);
        response.setRespondedAt(LocalDateTime.now());
        return donorResponseRepository.save(response);
    }

    private DonorResponse getDonorResponseForRequest(Integer userId, Integer requestId) {
        Donor donor = donorRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));

        return donorResponseRepository.findByBloodRequestRequestIdAndDonorDonorId(requestId, donor.getDonorId())
                .orElseThrow(() -> new RuntimeException("Request not assigned to this donor"));
    }
}
