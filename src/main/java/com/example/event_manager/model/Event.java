package com.example.event_manager.model;

import com.example.event_manager.model.User;

import jakarta.persistence.*;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;

    // If you want: start and end date fields
    // private String startDate;
    // private String endDate;

    // Associate this event with its owner/creator:
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Event() {}

    public Event(String title, String description, String location, User user) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.user = user;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public User getUser() { return user; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setUser(User user) { this.user = user; }
}
