package com.example.event_manager.dto;

public class SignupFromInviteDto {

    private String email;
    private String password;
    private Long eventId;
    private String token;

    public SignupFromInviteDto() {
    }

    public SignupFromInviteDto(String email, String password, Long eventId, String token) {
        this.email = email;
        this.password = password;
        this.eventId = eventId;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
