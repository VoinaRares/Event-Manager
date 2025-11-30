package com.example.event_manager.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class InvitationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "token", nullable = false, unique = true, length = 64)
    private String token;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "consumed", nullable = false)
    private boolean consumed = false;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public InvitationToken() {
    }

    public InvitationToken(Long eventId, String token, String email,LocalDateTime expiresAt) {
        this.eventId = eventId;
        this.token = token;
        this.email = email;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getToken() {
        return token;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getEmail() {
        return email;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
