package com.example.event_manager.service;

import com.example.event_manager.dto.AdminEventSummaryDto;
import com.example.event_manager.dto.AdminHomeResponseDto;
import com.example.event_manager.dto.AdminStatsDto;
import com.example.event_manager.model.Event;
import com.example.event_manager.model.EventParticipant;
import com.example.event_manager.model.Organizer;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.repository.OrganizerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final OrganizerRepository organizerRepository;
    private final EventRepository eventRepository;

    public AdminDashboardService(OrganizerRepository organizerRepository, EventRepository eventRepository) {
        this.organizerRepository = organizerRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public AdminHomeResponseDto getHomeData(Long organizerId) {
        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found with id: " + organizerId));

        List<Event> events = eventRepository.findByOrganizers_Id(organizerId);
        List<AdminEventSummaryDto> summaries = events.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());

        AdminHomeResponseDto response = new AdminHomeResponseDto();
        response.setOrganizerId(organizer.getId());
        response.setOrganizerName(organizer.getName());
        response.setOrganizerEmail(organizer.getEmail());
        response.setStats(buildStats(summaries));
        response.setEvents(summaries);
        return response;
    }

    private AdminEventSummaryDto toSummaryDto(Event event) {
        AdminEventSummaryDto dto = new AdminEventSummaryDto();
        dto.setEventId(event.getId());
        dto.setName(event.getName());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setLongitude(event.getLongitude());
        dto.setLatitude(event.getLatitude());
        dto.setPlaceId(event.getPlaceId());
        dto.setLocationName(event.getLocationName());
        dto.setPhotoCount(event.getPhotos() != null ? event.getPhotos().size() : 0);

        int invited = event.getParticipants() != null ? event.getParticipants().size() : 0;
        long confirmed = event.getParticipants() != null
                ? event.getParticipants().stream().filter(EventParticipant::isComing).count()
                : 0;
        long responded = event.getParticipants() != null
                ? event.getParticipants().stream().filter(EventParticipant::isResponded).count()
                : 0;
        int pending = Math.max(invited - (int) responded, 0);

        dto.setInvitedGuests(invited);
        dto.setConfirmedGuests((int) confirmed);
        dto.setPendingGuests(pending);
        dto.setStatus(determineStatus(event.getStartDate(), event.getEndDate()));
        return dto;
    }

    private AdminStatsDto buildStats(List<AdminEventSummaryDto> events) {
        AdminStatsDto stats = new AdminStatsDto();
        stats.setTotalEvents(events.size());
        stats.setActiveEvents((int) events.stream().filter(e -> "ACTIVE".equals(e.getStatus())).count());
        stats.setUpcomingEvents((int) events.stream().filter(e -> "UPCOMING".equals(e.getStatus())).count());
        stats.setPastEvents((int) events.stream().filter(e -> "COMPLETED".equals(e.getStatus())).count());
        stats.setInvitedGuests(events.stream().mapToInt(AdminEventSummaryDto::getInvitedGuests).sum());
        stats.setConfirmedGuests(events.stream().mapToInt(AdminEventSummaryDto::getConfirmedGuests).sum());
        stats.setPendingGuests(events.stream().mapToInt(AdminEventSummaryDto::getPendingGuests).sum());
        return stats;
    }

    private String determineStatus(String startDate, String endDate) {
        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);
        if (start == null || end == null) {
            return "UNKNOWN";
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(start)) {
            return "UPCOMING";
        }
        if ((today.isEqual(start) || today.isAfter(start)) && today.isBefore(end.plusDays(1))) {
            return "ACTIVE";
        }
        if (today.isAfter(end)) {
            return "COMPLETED";
        }
        return "UNKNOWN";
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            try {
                return LocalDate.parse(value.trim(), java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH));
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }
    }
}
