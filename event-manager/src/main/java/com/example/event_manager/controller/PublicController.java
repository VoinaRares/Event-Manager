package com.proiectcolectiv.eventmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class PublicController {

    @GetMapping("/api/public/summary")
    public Map<String,Object> summary() {
        return Map.of(
            "appName", "Event Manager",
            "activeEvents", 5,
            "registeredUsers", 120
        );
    }
}
