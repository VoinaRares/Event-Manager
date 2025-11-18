package com.example.event_manager.controller;

import com.example.event_manager.model.Event;
import com.example.event_manager.model.Organizer;
import com.example.event_manager.model.User;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.repository.UserRepository;
import com.example.event_manager.dto.EventCreateDto;
import com.example.event_manager.dto.EventResponseDto;
import com.example.event_manager.mapper.EventMapper;
import com.example.event_manager.service.EventParticipantService;
import com.example.event_manager.service.OrganizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200")
public class EventController {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final OrganizerService organizerService;
    private final EventParticipantService participantService;

    public EventController(
            EventRepository eventRepository,
            UserRepository userRepository,
            OrganizerService organizerService,
            EventParticipantService participantService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.organizerService = organizerService;
        this.participantService = participantService;
    }

    @GetMapping
    public List<EventResponseDto> getAllEvents() {
        return EventMapper.toDtoList(eventRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventCreateDto dto) {
        if (dto.getOrganizers() == null || dto.getOrganizers().size() != 2) {
            return ResponseEntity.badRequest().body("Event must have exactly 2 organizers");
        }

        Set<Organizer> organizers = dto.getOrganizers().stream()
                .map(organizerDto -> organizerService.findOrCreateEntity(organizerDto))
                .collect(Collectors.toSet());

        Event event = new Event(
                dto.getName(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getLongitude(),
                dto.getLatitude(),
                dto.getPlaceId()
        );
        event.setOrganizers(organizers);

        Event saved = eventRepository.save(event);
        return ResponseEntity.ok(EventMapper.toDto(saved));
    }

    @PostMapping("/{eventId}/invite/{userId}")
    public ResponseEntity<?> inviteParticipant(@PathVariable Long eventId, @PathVariable Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        participantService.inviteParticipant(event, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/accept/{token}")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long eventId, @PathVariable String token) {
        participantService.acceptInvitation(eventId, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/reject/{token}")
    public ResponseEntity<?> rejectInvitation(@PathVariable Long eventId, @PathVariable String token) {
        participantService.rejectInvitation(eventId, token);
        return ResponseEntity.ok().build();
    }
}
