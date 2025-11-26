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
import java.security.SecureRandom;
import java.math.BigInteger;

@Service
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EventParticipantService(
            EventParticipantRepository eventParticipantRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

        private String generateToken() {
                SecureRandom random = new SecureRandom();
                return new BigInteger(256, random).toString(32);
        }

        @Transactional
        public void inviteParticipant(Event event, User user) {
                String token = generateToken();
                EventParticipant participant = new EventParticipant(event, user, token);
                eventParticipantRepository.save(participant);
                event.addParticipant(participant);
                eventRepository.save(event);
                String link = "http://localhost:4200/invite/" + event.getId() + "/" + token;
                String body = "You have been invited to the event: " + event.getName() +
                        "\nClick this link to respond: " + link;
                emailService.sendEmail(user.getEmail(), "You have been invited to the event!", body);
        }

    @Transactional
    public void acceptInvitation(Long eventId, String token) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        EventParticipant participant = eventParticipantRepository.findByEventAndToken(event, token)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found for token: " + token));
        participant.setComing(true);
        participant.setResponded(true);
        eventParticipantRepository.save(participant);
    }

    @Transactional
    public void rejectInvitation(Long eventId, String token) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        EventParticipant participant = eventParticipantRepository.findByEventAndToken(event, token)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found for token: " + token));
        eventParticipantRepository.delete(participant);
    }

    @Transactional
    public void acceptInvitation(Long eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        EventParticipant participant = eventParticipantRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found for user: " + userId));
        participant.setComing(true);
        participant.setResponded(true);
        eventParticipantRepository.save(participant);
    }

    @Transactional
    public void rejectInvitation(Long eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        EventParticipant participant = eventParticipantRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found for user: " + userId));
        eventParticipantRepository.delete(participant);
    }
}
