package com.example.event_manager.dto;

public class AdminStatsDto {
    private int totalEvents;
    private int activeEvents;
    private int upcomingEvents;
    private int pastEvents;
    private int invitedGuests;
    private int confirmedGuests;
    private int pendingGuests;

    public int getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(int totalEvents) {
        this.totalEvents = totalEvents;
    }

    public int getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(int activeEvents) {
        this.activeEvents = activeEvents;
    }

    public int getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(int upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public int getPastEvents() {
        return pastEvents;
    }

    public void setPastEvents(int pastEvents) {
        this.pastEvents = pastEvents;
    }

    public int getInvitedGuests() {
        return invitedGuests;
    }

    public void setInvitedGuests(int invitedGuests) {
        this.invitedGuests = invitedGuests;
    }

    public int getConfirmedGuests() {
        return confirmedGuests;
    }

    public void setConfirmedGuests(int confirmedGuests) {
        this.confirmedGuests = confirmedGuests;
    }

    public int getPendingGuests() {
        return pendingGuests;
    }

    public void setPendingGuests(int pendingGuests) {
        this.pendingGuests = pendingGuests;
    }
}

