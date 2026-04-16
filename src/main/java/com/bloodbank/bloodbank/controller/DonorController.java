package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.model.User;
import com.bloodbank.bloodbank.model.BloodRequest.RequestStatus;
import com.bloodbank.bloodbank.model.Notification;
import com.bloodbank.bloodbank.model.DonorResponse;
import com.bloodbank.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.bloodbank.repository.DonationHistoryRepository;
import com.bloodbank.bloodbank.repository.DonorRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.DonorService;
import com.bloodbank.bloodbank.service.NotificationService;
import com.bloodbank.bloodbank.service.DonorResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/donor")
public class DonorController {

    @Autowired private UserRepository userRepository;
    @Autowired private DonorRepository donorRepository;
    @Autowired private DonationHistoryRepository donationHistoryRepository;
    @Autowired private BloodRequestRepository bloodRequestRepository;
    @Autowired private DonorService donorService;
    @Autowired private NotificationService notificationService;
    @Autowired private DonorResponseService donorResponseService;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        User user = getUser(principal);

        if (!donorService.hasProfile(user.getUserId())) {
            return "redirect:/donor/setup";
        }

        var notifications = notificationService.getUnreadNotifications(user.getUserId());
        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", notifications.size());
        model.addAttribute("pendingRequests", bloodRequestRepository.countByStatus(RequestStatus.PENDING));

        donorRepository.findByUserUserId(user.getUserId()).ifPresent(donor -> {
            model.addAttribute("donor", donor);
            model.addAttribute("totalDonations", donationHistoryRepository.countByDonorDonorId(donor.getDonorId()));
        });

        return "donor/dashboard";
    }

    @GetMapping("/setup")
    public String setupPage(Principal principal, Model model) {
        User user = getUser(principal);
        model.addAttribute("user", user);
        donorRepository.findByUserUserId(user.getUserId()).ifPresent(donor -> model.addAttribute("donor", donor));
        return "donor/setup";
    }

    @PostMapping("/setup")
    public String saveSetup(
            @RequestParam String bloodGroup,
            @RequestParam String city,
            @RequestParam String phone,
            Principal principal,
            Model model) {

        try {
            User user = getUser(principal);
            donorService.saveDonor(user, bloodGroup, city, phone);
            return "redirect:/donor/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "donor/setup";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Principal principal, Model model) {
        User user = getUser(principal);
        if (!donorService.hasProfile(user.getUserId())) {
            return "redirect:/donor/setup";
        }

        model.addAttribute("username", principal.getName());
        model.addAttribute("user", user);
        donorRepository.findByUserUserId(user.getUserId()).ifPresent(donor -> model.addAttribute("donor", donor));
        return "donor/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String bloodGroup,
            @RequestParam String city,
            @RequestParam String phone,
            @RequestParam(required = false) String availabilityStatus,
            Principal principal,
            Model model) {

        try {
            User user = getUser(principal);
            donorService.saveDonor(user, bloodGroup, city, phone);
            if (availabilityStatus != null && !availabilityStatus.isBlank()) {
                donorService.updateAvailabilityStatus(user.getUserId(), availabilityStatus);
            }
            return "redirect:/donor/profile?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return profilePage(principal, model);
        }
    }

    @GetMapping("/donations")
    public String donationsPage(Principal principal, Model model) {
        User user = getUser(principal);
        if (!donorService.hasProfile(user.getUserId())) {
            return "redirect:/donor/setup";
        }

        var donor = donorRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));

        model.addAttribute("username", principal.getName());
        model.addAttribute("donor", donor);
        model.addAttribute("donations", donationHistoryRepository.findByDonorDonorIdOrderByDonationDateDesc(donor.getDonorId()));
        model.addAttribute("totalDonations", donationHistoryRepository.countByDonorDonorId(donor.getDonorId()));
        return "donor/donations";
    }

    @GetMapping("/notifications")
    public String notificationsPage(Principal principal, Model model) {
        User user = getUser(principal);
        List<Notification> notifications = notificationService.getAllNotifications(user.getUserId());

        model.addAttribute("username", principal.getName());
        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", notifications.stream().filter(n -> !Boolean.TRUE.equals(n.getIsRead())).count());
        return "donor/notifications";
    }

    @GetMapping("/requests")
    public String requestsPage(Principal principal, Model model) {
        User user = getUser(principal);
        if (!donorService.hasProfile(user.getUserId())) {
            return "redirect:/donor/setup";
        }

        List<DonorResponse> requests = donorResponseService.getResponsesForDonor(user.getUserId());
        model.addAttribute("username", principal.getName());
        model.addAttribute("requests", requests);
        return "donor/requests";
    }

    @PostMapping("/requests/{requestId}/accept")
    public String acceptRequest(@PathVariable Integer requestId, Principal principal, Model model) {
        try {
            User user = getUser(principal);
            donorResponseService.acceptRequest(user.getUserId(), requestId);
            return "redirect:/donor/requests?updated=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return requestsPage(principal, model);
        }
    }

    @PostMapping("/requests/{requestId}/decline")
    public String declineRequest(@PathVariable Integer requestId, Principal principal, Model model) {
        try {
            User user = getUser(principal);
            donorResponseService.declineRequest(user.getUserId(), requestId);
            return "redirect:/donor/requests?updated=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return requestsPage(principal, model);
        }
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationAsRead(@PathVariable Integer id, Principal principal) {
        User user = getUser(principal);
        notificationService.markAsReadForUser(id, user.getUserId());
        return "redirect:/donor/notifications";
    }
}