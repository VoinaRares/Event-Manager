package com.example.event_manager.service;

import com.example.event_manager.model.Event;
import com.example.event_manager.model.EventParticipant;
import com.example.event_manager.model.User;
import com.example.event_manager.repository.EventParticipantRepository;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventParticipantService(
            EventParticipantRepository eventParticipantRepository,
            EventRepository eventRepository,
            UserRepository userRepository) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void inviteParticipant(Event event, User user) {
        EventParticipant participant = new EventParticipant(event, user);
        eventParticipantRepository.save(participant);
        event.addParticipant(participant);
        eventRepository.save(event);
        // TODO: Send email invitation
    }

    @Transactional
    public void acceptInvitation(Long eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
                eventParticipantRepository.findByEventAndUser(event, user)
                                .ifPresent(participant -> {
                                        participant.setComing(true);
                                        participant.setResponded(true);
                                        eventParticipantRepository.save(participant);
                                });
    }

    @Transactional
    public void rejectInvitation(Long eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        eventParticipantRepository.findByEventAndUser(event, user)
                .ifPresent(eventParticipantRepository::delete);
    }
}