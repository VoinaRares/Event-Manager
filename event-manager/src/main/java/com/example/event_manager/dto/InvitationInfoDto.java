package com.example.event_manager.dto;

import java.time.LocalDateTime;

public class InvitationInfoDto {

    private Long eventId;
    private String eventName;
    private String startDate;
    private String endDate;
    private String locationName;
    private String invitedEmail;
    private LocalDateTime expiresAt;
    private boolean expired;

    public InvitationInfoDto() {
    }

    public InvitationInfoDto(Long eventId, String eventName, String startDate,
            String endDate, String locationName, String invitedEmail,
            LocalDateTime expiresAt) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationName = locationName;
        this.invitedEmail = invitedEmail;
        this.expiresAt = expiresAt;
        this.expired = LocalDateTime.now().isAfter(expiresAt);
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
