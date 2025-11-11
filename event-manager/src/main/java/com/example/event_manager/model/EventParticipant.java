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

    public EventParticipant() {}

    public EventParticipant(Event event, User user) {
        this.id = new EventParticipantId(event.getId(), user.getId());
        this.event = event;
        this.user = user;
        this.isComing = false;
        this.responded = false;
        this.invitationSentAt = LocalDateTime.now();
    }

    public EventParticipantId getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        if (this.id == null) this.id = new EventParticipantId();
        this.id.setEventId(event != null ? event.getId() : null);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (this.id == null) this.id = new EventParticipantId();
        this.id.setUserId(user != null ? user.getId() : null);
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