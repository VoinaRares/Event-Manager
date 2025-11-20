package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    // Change Password - GET
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    // Change Password - POST
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 Model model,
                                 Principal principal) {
        User admin = userRepository.findByUsername(principal.getName());
        if (admin == null) {
            model.addAttribute("error", "Admin user not found.");
            return "change-password";
        }
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            model.addAttribute("error", "Old password is incorrect.");
            return "change-password";
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(admin);
        model.addAttribute("success", "Password changed successfully!");
        return "change-password";
    }
}
