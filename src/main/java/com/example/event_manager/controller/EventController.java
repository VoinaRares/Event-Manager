package com.example.event_manager.controller;

import com.example.event_manager.model.Event;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.model.AuditLog;
import com.example.event_manager.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/events")
public class EventController {

    private final EventRepository eventRepo;
    private final AuditLogRepository logRepo;

    public EventController(EventRepository eventRepo, AuditLogRepository logRepo) {
        this.eventRepo = eventRepo;
        this.logRepo = logRepo;
    }

    @GetMapping
    public String list(@RequestParam(required=false) String q, Model model) {
        if (q != null && !q.isEmpty()) {
            model.addAttribute("events", eventRepo.findByTitleContainingIgnoreCase(q));
        } else {
            model.addAttribute("events", eventRepo.findAll());
        }
        return "admin-events";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin-event-form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Event event, Authentication auth) {
        eventRepo.save(event);
        if (logRepo != null && auth != null)
            logRepo.save(new AuditLog(auth.getName(), "created event", event.getTitle(), java.time.LocalDateTime.now()));
        return "redirect:/admin/events";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventRepo.findById(id).orElseThrow());
        return "admin-event-form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute Event event, Authentication auth) {
        event.setId(id);
        eventRepo.save(event);
        if (logRepo != null && auth != null)
            logRepo.save(new AuditLog(auth.getName(), "edited event", event.getTitle(), java.time.LocalDateTime.now()));
        return "redirect:/admin/events";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Authentication auth) {
        Event e = eventRepo.findById(id).orElse(null);
        if (e != null) {
            eventRepo.deleteById(id);
            if (logRepo != null && auth != null)
                logRepo.save(new AuditLog(auth.getName(), "deleted event", e.getTitle(), java.time.LocalDateTime.now()));
        }
        return "redirect:/admin/events";
    }
}
