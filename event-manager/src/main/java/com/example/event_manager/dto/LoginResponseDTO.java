package com.example.event_manager.dto;

public class LoginResponseDTO {

    private Long id;
    private String email;

    public LoginResponseDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
