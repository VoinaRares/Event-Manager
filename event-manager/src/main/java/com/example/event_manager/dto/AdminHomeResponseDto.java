package com.example.event_manager.dto;

import java.util.List;

public class AdminHomeResponseDto {
    private Long organizerId;
    private String organizerName;
    private String organizerEmail;
    private AdminStatsDto stats;
    private List<AdminEventSummaryDto> events;

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }

    public AdminStatsDto getStats() {
        return stats;
    }

    public void setStats(AdminStatsDto stats) {
        this.stats = stats;
    }

    public List<AdminEventSummaryDto> getEvents() {
        return events;
    }

    public void setEvents(List<AdminEventSummaryDto> events) {
        this.events = events;
    }
}

