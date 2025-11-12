package com.example.event_manager.dto;

public class AdminEventSummaryDto {
    private Long eventId;
    private String name;
    private String startDate;
    private String endDate;
    private String status;
    private Double longitude;
    private Double latitude;
    private Long placeId;
    private String locationName;
    private int photoCount;
    private int invitedGuests;
    private int confirmedGuests;
    private int pendingGuests;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
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
