package com.example.event_manager.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.event_manager.service.OrganizerService;

import com.example.event_manager.dto.LoginRequestDTO;
import com.example.event_manager.dto.LoginResponseDTO;
import com.example.event_manager.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class LoginController {
    private final UserService userService;
    private final OrganizerService organizerService;

    public LoginController(UserService userService, OrganizerService organizerService) {
        this.userService = userService;
        this.organizerService = organizerService;
    }

    @PostMapping("/users/auth/login")
    public LoginResponseDTO login_user(@RequestBody LoginRequestDTO request) {
        return userService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/users/auth/refresh")
    public LoginResponseDTO refreshToken_user(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid refresh token");
        }

        String refreshToken = authHeader.substring(7);
        return userService.refreshAccessToken(refreshToken);
    }

    @PostMapping("/organizers/auth/login")
    public LoginResponseDTO login_organizer(@RequestBody LoginRequestDTO request) {
        return organizerService.login(request.getEmail(), request.getPassword());
    }

}
