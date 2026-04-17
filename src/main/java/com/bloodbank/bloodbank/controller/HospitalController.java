package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.model.User;
import com.bloodbank.bloodbank.model.BloodRequest.RequestStatus;
import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.BloodInventory;
import com.bloodbank.bloodbank.model.Hospital;
import com.bloodbank.bloodbank.repository.BloodInventoryRepository;
import com.bloodbank.bloodbank.repository.DonorRepository;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import com.bloodbank.bloodbank.repository.UserRepository;
import com.bloodbank.bloodbank.service.BloodRequestService;
import com.bloodbank.bloodbank.service.HospitalService;
import com.bloodbank.bloodbank.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/hospital")
public class HospitalController {

    @Autowired private UserRepository userRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private BloodInventoryRepository bloodInventoryRepository;
    @Autowired private DonorRepository donorRepository;
    @Autowired private BloodRequestService bloodRequestService;
    @Autowired private HospitalService hospitalService;
    @Autowired private InventoryService inventoryService;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("totalDonors", donorRepository.count());
        User user = getUser(principal);

        if (!hospitalService.hasProfile(user.getUserId())) {
            return "redirect:/hospital/setup";
        }

        hospitalRepository.findByUserUserId(user.getUserId()).ifPresent(hospital -> {
            var requests = bloodRequestService.getRequestsByHospital(hospital.getHospitalId());
            long pending = requests.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count();
            long matched = requests.stream().filter(r -> r.getStatus() == RequestStatus.MATCHED).count();
            long units = bloodInventoryRepository.findByHospitalHospitalId(hospital.getHospitalId())
                    .stream()
                    .mapToLong(inv -> inv.getUnitsAvailable() == null ? 0 : inv.getUnitsAvailable())
                    .sum();

            model.addAttribute("activeRequestCount", pending + matched);
            model.addAttribute("matchedRequestCount", matched);
            model.addAttribute("bloodUnitsAvailable", units);
            model.addAttribute("recentRequests", requests);
        });

        return "hospital/dashboard";
    }

    @GetMapping("/donors")
    public String donorsPage(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());

        User user = getUser(principal);
        if (!hospitalService.hasProfile(user.getUserId())) {
            return "redirect:/hospital/setup";
        }

        List<Donor> donors = donorRepository.findAll().stream()
                .sorted(Comparator
                        .comparingInt((Donor d) -> d.getAvailabilityStatus() == Donor.AvailabilityStatus.AVAILABLE ? 0 : 1)
                        .thenComparing(d -> d.getBloodGroup().name())
                        .thenComparing(d -> d.getUser().getUsername(), String.CASE_INSENSITIVE_ORDER))
                .toList();

        long availableDonors = donors.stream()
                .filter(d -> d.getAvailabilityStatus() == Donor.AvailabilityStatus.AVAILABLE)
                .count();

        model.addAttribute("donors", donors);
        model.addAttribute("totalDonors", donors.size());
        model.addAttribute("availableDonors", availableDonors);
        return "hospital/donors";
    }

    @GetMapping("/setup")
    public String setupPage() {
        return "hospital/setup";
    }

    @GetMapping("/inventory")
    public String inventoryPage(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());

        User user = getUser(principal);
        Hospital hospital = hospitalRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Hospital profile not found. Please complete setup."));

        List<BloodInventory> inventory = inventoryService.getInventoryByHospital(hospital.getHospitalId());
        long totalUnits = inventoryService.getTotalUnits(hospital.getHospitalId());

        model.addAttribute("inventory", inventory);
        model.addAttribute("totalUnits", totalUnits);
        return "hospital/inventory";
    }

    @PostMapping("/inventory")
    public String addInventory(
            @RequestParam String bloodGroup,
            @RequestParam int units,
            @RequestParam(required = false) String expiryDate,
            Principal principal,
            Model model) {

        try {
            User user = getUser(principal);
            Hospital hospital = hospitalRepository.findByUserUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Hospital profile not found. Please complete setup."));

            inventoryService.upsertInventory(hospital.getHospitalId(), bloodGroup, units, expiryDate);
            return "redirect:/hospital/inventory?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return inventoryPage(principal, model);
        }
    }

    @PostMapping("/setup")
    public String saveSetup(
            @RequestParam String hospitalName,
            @RequestParam String licenseNumber,
            @RequestParam String address,
            @RequestParam String city,
            Principal principal,
            Model model) {

        try {
            User user = getUser(principal);
            hospitalService.saveHospital(user, hospitalName, licenseNumber, address, city);
            return "redirect:/hospital/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "hospital/setup";
        }
    }
}