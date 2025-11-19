package com.example.event_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user") // Optional: use your actual table name
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Username must be unique!
    private String username;

    @Column(nullable = false)
    private String password; // Store hashed password (e.g., BCrypt)

    @Column(nullable = false)
    private String role; // Should be "USER" or "ADMIN"

    // Constructors
    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }
}
