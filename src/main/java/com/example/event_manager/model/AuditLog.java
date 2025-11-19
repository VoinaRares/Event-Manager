package com.example.event_manager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actor;      // Who performed the action
    private String action;     // What action (e.g., "deleted user", "created event")
    private String target;     // Who or what was affected
    private LocalDateTime timestamp;

    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(String actor, String action, String target, LocalDateTime timestamp) {
        this.actor = actor;
        this.action = action;
        this.target = target;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getTarget() { return target; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setActor(String actor) { this.actor = actor; }
    public void setAction(String action) { this.action = action; }
    public void setTarget(String target) { this.target = target; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
