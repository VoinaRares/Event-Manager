package com.example.event_manager.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.event_manager.dto.LoginRequestDTO;
import com.example.event_manager.dto.LoginResponseDTO;
import com.example.event_manager.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users/auth")
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return userService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid refresh token");
        }

        String refreshToken = authHeader.substring(7);
        return userService.refreshAccessToken(refreshToken);
    }

}
