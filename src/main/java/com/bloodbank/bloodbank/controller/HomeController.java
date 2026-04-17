package com.bloodbank.bloodbank.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        String role = authentication.getAuthorities()
                .iterator().next().getAuthority();

        return switch (role) {
            case "ROLE_ADMIN"    -> "redirect:/admin/dashboard";
            case "ROLE_HOSPITAL" -> "redirect:/hospital/dashboard";
            case "ROLE_DONOR"    -> "redirect:/donor/dashboard";
            default              -> "auth/login";
        };
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }
}