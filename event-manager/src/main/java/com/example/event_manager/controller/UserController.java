package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.model.Event;
import com.example.event_manager.repository.UserRepository;
import com.example.event_manager.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.HashSet;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // List all users
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    // Show create user form
    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-create-form";
    }

    // Handle user creation (with password encoding)
    @PostMapping("/save")
    public String saveNewUser(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // Show edit user form
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        return "user-form";
    }

    // Handle user update (does not re-encode password)
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user) {
        // Optional: if you want to allow editing password here, encode it before saving!
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // Show user profile
    @GetMapping("/profile/{id}")
    public String showUserProfile(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        model.addAttribute("events", user.getEvents());
        return "user-profile";
    }

    // Delete user
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // Show assign events form
    @GetMapping("/assign-events/{id}")
    public String showAssignEventsForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        model.addAttribute("events", eventRepository.findAll());
        return "assign-events";
    }

    // Handle assign events
    @PostMapping("/assign-events")
    public String assignEvents(@RequestParam Long userId,
                               @RequestParam(required=false) Set<Long> eventIds) {
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
}
