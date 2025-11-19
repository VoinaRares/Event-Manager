package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.model.Role;
import com.example.event_manager.model.AuditLog;
import com.example.event_manager.repository.UserRepository;
import com.example.event_manager.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {
        // Check username uniqueness
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists!");
            model.addAttribute("roles", Role.values());
            return "register";
        }
        // Check password confirmation
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            model.addAttribute("roles", Role.values());
            return "register";
        }
        // Check password strength
        if (!user.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            model.addAttribute("error", "Password must be at least 8 characters, with letters and digits.");
            model.addAttribute("roles", Role.values());
            return "register";
        }
        // Role fallback (defaults to USER)
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // Optionally, log registration in audit
        auditLogRepository.save(new AuditLog(user.getUsername(), "registered", user.getUsername(), LocalDateTime.now()));

        return "redirect:/login?registered";
    }
}
