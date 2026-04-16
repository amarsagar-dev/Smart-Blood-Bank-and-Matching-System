package com.bloodbank.bloodbank.controller;

import com.bloodbank.bloodbank.model.User;
import com.bloodbank.bloodbank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            Model model) {

        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already taken.");
            return "auth/register";
        }
        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email already registered.");
            return "auth/register";
        }

        userService.registerUser(username, email, password, User.Role.valueOf(role));
        return "redirect:/auth/login?registered=true";
    }
}