package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.model.BloodRequest.RequestStatus;
import com.bloodbank.bloodbank.model.User.Role;
import com.bloodbank.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.bloodbank.repository.DonorRepository;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Arrays;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private DonorRepository donorRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private BloodRequestRepository bloodRequestRepository;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalDonors", donorRepository.count());
        model.addAttribute("totalHospitals", hospitalRepository.count());

        long activeRequests = bloodRequestRepository.countByStatus(RequestStatus.PENDING)
                + bloodRequestRepository.countByStatus(RequestStatus.MATCHED);
        model.addAttribute("activeRequests", activeRequests);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/requests")
    public String requests(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("requests", bloodRequestRepository.findAll());
        return "admin/requests";
    }

    @GetMapping("/report")
    public String report(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());

        long pending = bloodRequestRepository.countByStatus(RequestStatus.PENDING);
        long matched = bloodRequestRepository.countByStatus(RequestStatus.MATCHED);
        long fulfilled = bloodRequestRepository.countByStatus(RequestStatus.FULFILLED);
        long cancelled = bloodRequestRepository.countByStatus(RequestStatus.CANCELLED);

        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalDonors", donorRepository.count());
        model.addAttribute("totalHospitals", hospitalRepository.count());
        model.addAttribute("totalRequests", bloodRequestRepository.count());
        model.addAttribute("pendingRequests", pending);
        model.addAttribute("matchedRequests", matched);
        model.addAttribute("fulfilledRequests", fulfilled);
        model.addAttribute("cancelledRequests", cancelled);
        model.addAttribute("adminUsers", userRepository.countByRole(Role.ADMIN));
        model.addAttribute("donorUsers", userRepository.countByRole(Role.DONOR));
        model.addAttribute("hospitalUsers", userRepository.countByRole(Role.HOSPITAL));
        model.addAttribute("allStatuses", Arrays.asList(RequestStatus.values()));

        return "admin/report";
    }
}