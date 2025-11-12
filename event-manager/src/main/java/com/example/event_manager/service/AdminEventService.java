package com.example.event_manager.service;

import com.example.event_manager.dto.AdminEventCreateDto;
import com.example.event_manager.dto.AdminEventUpdateDto;
import com.example.event_manager.model.Event;
import com.example.event_manager.model.Organizer;
import com.example.event_manager.model.User;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.repository.OrganizerRepository;
import com.example.event_manager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class AdminEventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final UserRepository userRepository;
    private final EventParticipantService participantService;

    public AdminEventService(
            EventRepository eventRepository,
            OrganizerRepository organizerRepository,
            UserRepository userRepository,
            EventParticipantService participantService) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
        this.userRepository = userRepository;
        this.participantService = participantService;
    }

    @Transactional
    public Event create(AdminEventCreateDto dto) {
        Set<Organizer> organizers = fetchOrganizers(dto.getOrganizerIds());
        Event event = new Event(
                dto.getName(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getLongitude(),
                dto.getLatitude(),
                dto.getPlaceId()
        );
        event.setLocationName(dto.getLocationName());
        event.setOrganizers(organizers);
        return eventRepository.save(event);
    }

    @Transactional
    public Event update(Long id, AdminEventUpdateDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));

        if (dto.getName() != null) {
            event.setName(dto.getName());
        }
        if (dto.getStartDate() != null) {
            event.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            event.setEndDate(dto.getEndDate());
        }
        if (dto.getLongitude() != null) {
            event.setLongitude(dto.getLongitude());
        }
        if (dto.getLatitude() != null) {
            event.setLatitude(dto.getLatitude());
        }
        if (dto.getPlaceId() != null) {
            event.setPlaceId(dto.getPlaceId());
        }
        if (dto.getLocationName() != null) {
            event.setLocationName(dto.getLocationName());
        }

        return eventRepository.save(event);
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found with id " + id);
        }
        eventRepository.deleteById(id);
    }

    @Transactional
    public void inviteParticipant(Long eventId, String email) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + eventId));

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email, generateTemporaryPassword())));

        boolean alreadyInvited = event.getParticipants() != null && event.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(user.getId()));
        if (alreadyInvited) {
            throw new ValidationException("User already invited to this event");
        }

        participantService.inviteParticipant(event, user);
    }

    private Set<Organizer> fetchOrganizers(List<Long> ids) {
        if (ids == null || ids.size() != 2) {
            throw new ValidationException("Exactly two organizer IDs must be provided");
        }
        List<Organizer> organizers = organizerRepository.findAllById(ids);
        if (organizers.size() != 2) {
            throw new ValidationException("Both organizers must exist");
        }
        return organizers.stream().collect(Collectors.toSet());
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString();
    }
}
