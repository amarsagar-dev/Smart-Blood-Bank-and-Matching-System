package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.model.Hospital;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.BloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/hospital")
public class BloodRequestController {

    @Autowired
    private BloodRequestService bloodRequestService;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private UserRepository userRepository;

        private Hospital getHospitalForUser(Principal principal) {
        var user = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return hospitalRepository.findByUserUserId(user.getUserId())
            .orElseThrow(() -> new RuntimeException(
                "Hospital profile not found. Please complete your profile first."));
        }

    @GetMapping("/request")
    public String requestPage() {
        return "hospital/request";
    }

    @PostMapping("/request")
    public String submitRequest(
            @RequestParam String bloodGroup,
            @RequestParam int units,
            @RequestParam String urgency,
            Principal principal,
            Model model) {

        try {
            Hospital hospital = getHospitalForUser(principal);

            bloodRequestService.createRequest(hospital.getHospitalId(),
                                               bloodGroup, units, urgency);

            return "redirect:/hospital/requests?success=true";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "hospital/request";
        }
    }

    @GetMapping("/requests")
    public String viewRequests(Principal principal, Model model) {
        Hospital hospital = getHospitalForUser(principal);
        List<com.bloodbank.bloodbank.model.BloodRequest> requests =
            bloodRequestService.getRequestsByHospital(hospital.getHospitalId());
        model.addAttribute("requests", requests);
        model.addAttribute("acceptedDonorCounts", bloodRequestService.getAcceptedDonorCounts(requests));

        return "hospital/requests";
    }

    @PostMapping("/requests/{id}/cancel")
    public String cancelRequest(@PathVariable Integer id, Principal principal, Model model) {
        try {
            Hospital hospital = getHospitalForUser(principal);
            bloodRequestService.cancelRequest(hospital.getHospitalId(), id);
            return "redirect:/hospital/requests?updated=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return viewRequests(principal, model);
        }
    }

    @PostMapping("/requests/{id}/fulfill")
    public String fulfillRequest(@PathVariable Integer id, Principal principal, Model model) {
        try {
            Hospital hospital = getHospitalForUser(principal);
            bloodRequestService.fulfillRequest(hospital.getHospitalId(), id);
            return "redirect:/hospital/requests?updated=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return viewRequests(principal, model);
        }
    }
}