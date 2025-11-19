package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ForgotPasswordController {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    public ForgotPasswordController(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo; this.encoder = encoder;
    }

    @GetMapping("/forgot-password")
    public String form() { return "forgot-password"; }

    @PostMapping("/forgot-password")
    public String reset(@RequestParam String username,
                        @RequestParam String newPassword,
                        @RequestParam String confirmPassword,
                        Model model) {
        User user = repo.findByUsername(username);
        if(user == null) {
            model.addAttribute("error", "No such user.");
            return "forgot-password";
        }
        if(!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "forgot-password";
        }
        if(!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            model.addAttribute("error", "Password must be at least 8 characters and contain a digit and a letter.");
            return "forgot-password";
        }
        user.setPassword(encoder.encode(newPassword));
        repo.save(user);
        model.addAttribute("message", "Password reset. You can now login.");
        return "forgot-password";
    }
}
