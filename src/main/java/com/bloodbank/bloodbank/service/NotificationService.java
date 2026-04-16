package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.Notification;
import com.bloodbank.bloodbank.model.Notification.NotificationType;
import com.bloodbank.bloodbank.model.BloodRequest;
import com.bloodbank.bloodbank.model.DonorResponse;
import com.bloodbank.bloodbank.model.DonorResponse.ResponseStatus;
import com.bloodbank.bloodbank.pattern.DonorObserver;
import com.bloodbank.bloodbank.repository.DonorResponseRepository;
import com.bloodbank.bloodbank.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService implements DonorObserver {

    private final NotificationRepository notificationRepository;
    private final DonorResponseRepository donorResponseRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            DonorResponseRepository donorResponseRepository) {
        this.notificationRepository = notificationRepository;
        this.donorResponseRepository = donorResponseRepository;
    }

    @Override
    public void update(Donor donor, BloodRequest request) {
        Notification notification = new Notification();
        notification.setUser(donor.getUser());
        notification.setType(NotificationType.EMERGENCY);
        notification.setMessage(
            "URGENT: " + request.getUrgencyLevel() + " blood request! " +
            "Blood group " + request.getBloodGroup() + " needed at " +
            request.getHospital().getHospitalName() +
            ". Units needed: " + request.getUnitsNeeded()
        );
        notification.setIsRead(false);
        notificationRepository.save(notification);

        DonorResponse donorResponse = donorResponseRepository
                .findByBloodRequestRequestIdAndDonorDonorId(request.getRequestId(), donor.getDonorId())
                .orElseGet(DonorResponse::new);

        donorResponse.setBloodRequest(request);
        donorResponse.setDonor(donor);
        donorResponse.setResponseStatus(ResponseStatus.NOTIFIED);
        donorResponse.setRespondedAt(null);
        donorResponseRepository.save(donorResponse);
    }

    public void notifyMatchingDonors(List<Donor> donors, BloodRequest request) {
        for (Donor donor : donors) {
            update(donor, request);
        }
    }

    public List<Notification> getUnreadNotifications(Integer userId) {
        return notificationRepository.findByUserUserIdAndIsRead(userId, false);
    }

    public List<Notification> getAllNotifications(Integer userId) {
        return notificationRepository.findByUserUserIdOrderBySentAtDesc(userId);
    }

    public void markAsRead(Integer notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAsReadForUser(Integer notificationId, Integer userId) {
        notificationRepository.findByNotificationIdAndUserUserId(notificationId, userId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }
}