package com.example.event_manager.dto;

import java.util.List;

public class EventCreateDto {
    private String name;
    private String startDate;
    private String endDate;
    private Double longitude;
    private Double latitude;
    private Long placeId;
    private List<OrganizerCreateDto> organizers;

    public EventCreateDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Long getPlaceId() { return placeId; }
    public void setPlaceId(Long placeId) { this.placeId = placeId; }

    public List<OrganizerCreateDto> getOrganizers() { return organizers; }
    public void setOrganizers(List<OrganizerCreateDto> organizers) { this.organizers = organizers; }
}
