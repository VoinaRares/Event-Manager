package com.example.event_manager.controller;
import com.example.event_manager.repository.UserRepository;
import com.example.event_manager.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {
    private final UserRepository repo;
    public ProfileController(UserRepository repo) { this.repo = repo; }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = repo.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        return "profile";
    }
}
