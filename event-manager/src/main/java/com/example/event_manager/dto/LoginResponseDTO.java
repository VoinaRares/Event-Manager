package com.example.event_manager.dto;

public class LoginResponseDTO {
    private Long id;
    private String email;
    private String accessToken;
    private String refreshToken;

    public LoginResponseDTO(Long id, String email, String accessToken, String refreshToken) {
        this.id = id;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}