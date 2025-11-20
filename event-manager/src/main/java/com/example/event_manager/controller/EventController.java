package com.example.event_manager.controller;

import com.example.event_manager.model.Event;
import com.example.event_manager.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // List all events
    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "admin-events";
    }

    // Show create event form
    @GetMapping("/new")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "event-form";
    }

    // Save new event
    @PostMapping("/save")
    public String saveEvent(@ModelAttribute Event event) {
        eventRepository.save(event);
        return "redirect:/admin/events";
    }

    // Show edit event form
    @GetMapping("/edit/{id}")
    public String showEditEventForm(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) return "redirect:/admin/events";
        model.addAttribute("event", event);
        return "event-form";
    }

    // Update existing event
    @PostMapping("/update")
    public String updateEvent(@ModelAttribute Event event) {
        eventRepository.save(event);
        return "redirect:/admin/events";
    }

    // Delete event
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return "redirect:/admin/events";
    }
}
