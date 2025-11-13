package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChangePasswordController {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    public ChangePasswordController(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping("/change-password")
    public String form() { return "change-password"; }

    @PostMapping("/change-password")
    public String change(@RequestParam String oldPassword,
                         @RequestParam String newPassword,
                         @RequestParam String confirmPassword,
                         Authentication authentication,
                         Model model) {
        User user = repo.findByUsername(authentication.getName());
        if(!encoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Current password incorrect.");
            return "change-password";
        }
        if(!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "change-password";
        }
        if(!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            model.addAttribute("error", "Password must be at least 8 characters and contain a digit and a letter.");
            return "change-password";
        }
        user.setPassword(encoder.encode(newPassword));
        repo.save(user);
        model.addAttribute("message", "Password changed successfully.");
        return "change-password";
    }
}
