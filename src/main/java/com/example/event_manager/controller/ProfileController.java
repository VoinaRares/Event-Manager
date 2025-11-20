package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/dashboard")
    public String userDashboard(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/user/events")
    public String myEvents(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("events", user.getEvents());
        return "my-events";
    }
}
