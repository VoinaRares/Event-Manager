package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class MyEventsController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-events")
    public String myEvents(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        Optional<User> userWithEventsOpt = userRepository.findByIdWithEvents(user.getId());
        if (userWithEventsOpt.isEmpty()) {
            return "redirect:/login";
        }
        user = userWithEventsOpt.get();

        model.addAttribute("events", user.getEvents());
        return "my-events";
    }
}
