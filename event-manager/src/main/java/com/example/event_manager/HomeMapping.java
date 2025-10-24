package com.example.event_manager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeMapping {
    @GetMapping("/")
    public String home() {
        return "Hello, world!";
    }
}

