package com.example.event_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class AdminEventCreateDto {
    @NotBlank
    private String name;

    @NotBlank
    private String startDate;

    @NotBlank
    private String endDate;

    private Double longitude;
    private Double latitude;
    private Long placeId;
    private String locationName;

    @NotEmpty
    @Size(min = 2, max = 2, message = "Exactly two organizers are required")
    private List<Long> organizerIds;

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

    public List<Long> getOrganizerIds() {
        return organizerIds;
    }

    public void setOrganizerIds(List<Long> organizerIds) {
        this.organizerIds = organizerIds;
    }
}
