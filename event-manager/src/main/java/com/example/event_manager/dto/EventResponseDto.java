package com.example.event_manager.dto;

import java.util.List;

public class EventResponseDto {
    private Long id;
    private String name;
    private String startDate;
    private String endDate;
    private Double longitude;
    private Double latitude;
    private Long placeId;
    private String locationName;
    private List<OrganizerResponseDto> organizers;
    private List<EventParticipantDto> participants;
    private List<ImageResponseDto> images;

    public EventResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public List<OrganizerResponseDto> getOrganizers() { return organizers; }
    public void setOrganizers(List<OrganizerResponseDto> organizers) { this.organizers = organizers; }

    public List<EventParticipantDto> getParticipants() { return participants; }
    public void setParticipants(List<EventParticipantDto> participants) { this.participants = participants; }

    public List<ImageResponseDto> getImages() { return images; }
    public void setImages(List<ImageResponseDto> images) { this.images = images; }
}
