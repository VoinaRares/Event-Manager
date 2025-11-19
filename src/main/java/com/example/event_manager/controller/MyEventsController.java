package com.example.event_manager.controller;

import com.example.event_manager.model.Event;
import com.example.event_manager.model.User;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class MyEventsController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-events")
    public String myEvents(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        List<Event> events = eventRepository.findByUserId(user.getId());
        model.addAttribute("events", events);
        return "my-events";
    }
}
