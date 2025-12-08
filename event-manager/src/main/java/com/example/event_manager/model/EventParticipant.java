package com.example.event_manager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_participants")
public class EventParticipant {

    @EmbeddedId
    private EventParticipantId id;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_coming", nullable = false)
    private boolean isComing;

    @Column(name = "responded", nullable = false)
    private boolean responded;

    @Column(nullable = false)
    private LocalDateTime invitationSentAt;

    private LocalDateTime responseAt;

    @Column(name = "token", nullable = false, unique = true, length = 64)
    private String token;

    public EventParticipant() {}

    public EventParticipant(Event event, User user, String token) {
        this.id = new EventParticipantId(event.getId(), user.getId());
        this.event = event;
        this.user = user;
        this.isComing = false;
        this.responded = false;
        this.invitationSentAt = LocalDateTime.now();
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public EventParticipantId getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }

    public boolean isComing() {
        return isComing;
    }

    public void setComing(boolean coming) {
        isComing = coming;
    }

    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
        if (responded) {
            this.responseAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getInvitationSentAt() {
        return invitationSentAt;
    }

    public LocalDateTime getResponseAt() {
        return responseAt;
    }
}
