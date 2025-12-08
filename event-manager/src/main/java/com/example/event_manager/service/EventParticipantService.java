package com.example.event_manager.service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.event_manager.model.Event;
import com.example.event_manager.model.EventParticipant;
import com.example.event_manager.model.InvitationToken;
import com.example.event_manager.model.User;
import com.example.event_manager.repository.EventParticipantRepository;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.repository.InvitationTokenRepository;
import com.example.event_manager.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EventParticipantService {

        private final EventParticipantRepository eventParticipantRepository;
        private final EventRepository eventRepository;
        private final UserRepository userRepository;
        private final InvitationTokenRepository invitationTokenRepository;
        private final EmailService emailService;

        public EventParticipantService(
                        EventParticipantRepository eventParticipantRepository,
                        EventRepository eventRepository,
                        UserRepository userRepository,
                        InvitationTokenRepository invitationTokenRepository,
                        EmailService emailService) {
                this.eventParticipantRepository = eventParticipantRepository;
                this.eventRepository = eventRepository;
                this.userRepository = userRepository;
                this.invitationTokenRepository = invitationTokenRepository;
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
        public void inviteParticipant(Event event, String email) {

                InvitationToken token = new InvitationToken();
                token.setEventId(event.getId());
                token.setEmail(email);
                token.setToken(generateToken());
                token.setExpiresAt(java.time.LocalDateTime.now().plusDays(7));
                invitationTokenRepository.save(token);

                String link = "http://localhost:4200/invite/" + event.getId() + "/" + token.getToken();
                String body = "You've been invited to " + event.getName() +
                                "\nUse this link to create an account and join: " + link;

                emailService.sendEmail(email, "You're invited!", body);

        }

        @Transactional
        public void acceptInvitation(Long eventId, String token) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
                EventParticipant participant = eventParticipantRepository.findByEventAndToken(event, token)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Invitation not found for token: " + token));
                participant.setComing(true);
                participant.setResponded(true);
                eventParticipantRepository.save(participant);
        }

        @Transactional
        public void rejectInvitation(Long eventId, String token) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
                EventParticipant participant = eventParticipantRepository.findByEventAndToken(event, token)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Invitation not found for token: " + token));
                participant.setComing(false);
                participant.setResponded(true);
                eventParticipantRepository.save(participant);
        }

        @Transactional
        public void acceptInvitationFromToken(Long eventId, String token, User user) {
                InvitationToken invitationToken = invitationTokenRepository.findByEventIdAndToken(eventId, token)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Invitation not found for token: " + token));

                if (LocalDateTime.now().isAfter(invitationToken.getExpiresAt())) {
                        throw new RuntimeException("Invitation token has expired");
                }

                if (invitationToken.isConsumed()) {
                        throw new RuntimeException("This invitation has already been used");
                }

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

                EventParticipant participant = new EventParticipant(event, user, invitationToken.getToken());
                participant.setComing(true);
                participant.setResponded(true);
                eventParticipantRepository.save(participant);
                event.addParticipant(participant);
                eventRepository.save(event);

                invitationToken.setConsumed(true);
                invitationTokenRepository.save(invitationToken);
        }

        @Transactional
        public void claimInvitationFromToken(Long eventId, String token, User user) {
                InvitationToken invitationToken = invitationTokenRepository.findByEventIdAndToken(eventId, token)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Invitation not found for token: " + token));

                if (LocalDateTime.now().isAfter(invitationToken.getExpiresAt())) {
                        throw new RuntimeException("Invitation token has expired");
                }

                if (invitationToken.isConsumed()) {
                        throw new RuntimeException("This invitation has already been used");
                }

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

                EventParticipant participant = new EventParticipant(event, user, generateToken());
                eventParticipantRepository.save(participant);
                event.addParticipant(participant);
                eventRepository.save(event);

                invitationToken.setConsumed(true);
                invitationTokenRepository.save(invitationToken);
        }

        @Transactional
        public void rejectInvitationFromToken(Long eventId, String token) {
                InvitationToken invitationToken = invitationTokenRepository.findByEventIdAndToken(eventId, token)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Invitation not found for token: " + token));

                if (LocalDateTime.now().isAfter(invitationToken.getExpiresAt())) {
                        throw new RuntimeException("Invitation token has expired");
                }

                if (invitationToken.isConsumed()) {
                        throw new RuntimeException("This invitation has already been used");
                }

                invitationToken.setConsumed(true);
                invitationTokenRepository.save(invitationToken);
        }

        @Transactional
        public void acceptInvitation(Long eventId, Integer userId) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
                EventParticipant participant = eventParticipantRepository.findByEventAndUser(event, user)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Invitation not found for user: " + userId));
                participant.setComing(true);
                participant.setResponded(true);
                eventParticipantRepository.save(participant);
        }

        @Transactional
        public void rejectInvitation(Long eventId, Integer userId) {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new EntityNotFoundException("Event not found"));
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));

                EventParticipant participant = eventParticipantRepository
                        .findByEventAndUser(event, user)
                        .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

                participant.setComing(false);
                participant.setResponded(true);
                eventParticipantRepository.save(participant);
        }

}

