package com.example.event_manager.dto;

public class EventHistoryDto {
    private Long eventId;
    private String eventName;
    private String startDate;
    private boolean coming;
    private boolean responded;

    public EventHistoryDto() {}

    public EventHistoryDto(Long eventId, String eventName, String startDate, boolean coming, boolean responded) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.startDate = startDate;
        this.coming = coming;
        this.responded = responded;
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public boolean isComing() { return coming; }
    public void setComing(boolean coming) { this.coming = coming; }

    public boolean isResponded() { return responded; }
    public void setResponded(boolean responded) { this.responded = responded; }
}
