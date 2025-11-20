package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.model.Event;
import com.example.event_manager.repository.UserRepository;
import com.example.event_manager.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --------------- DASHBOARD ----------------
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    // --------------- USER LIST ----------------
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    // ------------- CREATE USER FORM -----------
    @GetMapping("/users/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-create-form";
    }

    // ------------- CREATE USER HANDLER ----------
    @PostMapping("/users/save")
    public String saveNewUser(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // ------------- EDIT USER FORM --------------
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        return "user-form";
    }

    // ------------- UPDATE USER HANDLER --------------
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // ------------- DELETE USER (POST with CSRF) -------------
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // ------------- USER PROFILE ---------------
    @GetMapping("/users/profile/{id}")
    public String showUserProfile(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        model.addAttribute("events", user.getEvents());
        return "user-profile";
    }

    // ------------- ASSIGN EVENTS FORM ----------
    @GetMapping("/users/assign-events/{id}")
    public String showAssignEventsForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        model.addAttribute("events", eventRepository.findAll());
        return "assign-events";
    }

    // ------- ASSIGN EVENTS (POST: Save assignments) -------
    @PostMapping("/users/assign-events")
    public String assignEventsToUser(@RequestParam Long userId,
                                     @RequestParam(required = false) Set<Long> eventIds) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Set<Event> selectedEvents = new HashSet<>();
            if (eventIds != null) {
                for (Long eventId : eventIds) {
                    eventRepository.findById(eventId).ifPresent(selectedEvents::add);
                }
            }
            user.setEvents(selectedEvents);
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    // ----------- CHANGE PASSWORD (GET) -------------------
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    // ----------- CHANGE PASSWORD (POST) -----------------
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
