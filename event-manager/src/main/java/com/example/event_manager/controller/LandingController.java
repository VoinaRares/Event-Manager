package com.example.event_manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingController {

    @GetMapping({"/", "/home", "/landing"})
    public String landing(Model model) {
        // trimite date demo către view, dacă vrei
        model.addAttribute("appName", "Event Manager");
        model.addAttribute("tagline", "Digitalizează-ți nunta — invitații, program și fotografii într-un singur loc");
        return "index";
    }
}
