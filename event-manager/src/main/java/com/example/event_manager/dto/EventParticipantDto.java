package com.example.event_manager.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventParticipantDto {
    private Long eventId;
    private Integer userId;
    private String userEmail;
    @JsonProperty("isComing")
    private boolean isComing;
    @JsonProperty("responded")
    private boolean responded;
    private LocalDateTime invitationSentAt;
    private LocalDateTime responseAt;
    private String token;

    public EventParticipantDto() {}

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public boolean isComing() { return isComing; }
    public void setComing(boolean coming) { isComing = coming; }

    public boolean isResponded() { return responded; }
    public void setResponded(boolean responded) { this.responded = responded; }

    public LocalDateTime getInvitationSentAt() { return invitationSentAt; }
    public void setInvitationSentAt(LocalDateTime invitationSentAt) { this.invitationSentAt = invitationSentAt; }

    public LocalDateTime getResponseAt() { return responseAt; }
    public void setResponseAt(LocalDateTime responseAt) { this.responseAt = responseAt; }

    public String getToken(){ return token; }
    public void setToken(String token){ this.token = token; }
}
