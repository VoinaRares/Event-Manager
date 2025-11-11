package com.example.event_manager.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String startDate;

    @Column(nullable = false)
    private String endDate;

    private Double longitude;
    private Double latitude;
    private Long placeId;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "event_organizers",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "organizer_id")
    )
    private Set<Organizer> organizers = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventParticipant> participants = new HashSet<>();

    public Event() {}

    public Event(String name, String startDate, String endDate, Double longitude, Double latitude, Long placeId) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeId = placeId;
    }

    public Long getId() {
        return id;
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

    public List<Image> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Image> photos) {
        this.photos = photos;
    }

    public Set<Organizer> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(Set<Organizer> organizers) {
        if (organizers.size() != 2) {
            throw new IllegalArgumentException("An event must have exactly 2 organizers");
        }
        this.organizers = organizers;
    }

    public Set<EventParticipant> getParticipants() {
        return participants;
    }

    public void addOrganizer(Organizer organizer) {
        if (organizers.size() >= 2) {
            throw new IllegalArgumentException("An event cannot have more than 2 organizers");
        }
        organizers.add(organizer);
    }

    public void addParticipant(EventParticipant participant) {
        participants.add(participant);
    }

    public void removeParticipant(EventParticipant participant) {
        participants.remove(participant);
    }
}
