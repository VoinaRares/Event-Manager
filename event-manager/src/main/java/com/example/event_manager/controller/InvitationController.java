package com.example.event_manager.controller;

import com.example.event_manager.dto.InvitationInfoDto;
import com.example.event_manager.dto.SignupFromInviteDto;
import com.example.event_manager.dto.LoginResponseDTO;
import com.example.event_manager.model.Event;
import com.example.event_manager.model.InvitationToken;
import com.example.event_manager.model.User;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.repository.InvitationTokenRepository;
import com.example.event_manager.service.EventParticipantService;
import com.example.event_manager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationTokenRepository invitationTokenRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventParticipantService eventParticipantService;

    public InvitationController(InvitationTokenRepository invitationTokenRepository,
                                EventRepository eventRepository,
                                UserService userService,
                                EventParticipantService eventParticipantService) {
        this.invitationTokenRepository = invitationTokenRepository;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.eventParticipantService = eventParticipantService;
    }

    @GetMapping("/{eventId}/{token}/info")
    public ResponseEntity<InvitationInfoDto> getInvitationInfo(@PathVariable Long eventId, @PathVariable String token) {
        try {
            InvitationToken invitationToken = invitationTokenRepository.findByEventIdAndToken(eventId, token)
                    .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

            if (LocalDateTime.now().isAfter(invitationToken.getExpiresAt())) {
                return ResponseEntity.status(HttpStatus.GONE)
                        .body(new InvitationInfoDto());
            }

            if (invitationToken.isConsumed()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new InvitationInfoDto());
            }

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EntityNotFoundException("Event not found"));

            InvitationInfoDto dto = new InvitationInfoDto(
                    event.getId(),
                    event.getName(),
                    event.getStartDate(),
                    event.getEndDate(),
                    event.getLocationName(),
                    invitationToken.getEmail(),
                    invitationToken.getExpiresAt()
            );

            return ResponseEntity.ok(dto);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/signup-and-accept")
    public ResponseEntity<LoginResponseDTO> signupFromInvite(@RequestBody SignupFromInviteDto request) {
        try {
            InvitationToken invitationToken = invitationTokenRepository.findByEventIdAndToken(request.getEventId(), request.getToken())
                    .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

            if (LocalDateTime.now().isAfter(invitationToken.getExpiresAt())) {
                return ResponseEntity.status(HttpStatus.GONE).build();
            }

            if (invitationToken.isConsumed()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            if (!invitationToken.getEmail().equalsIgnoreCase(request.getEmail())) {
                return ResponseEntity.badRequest().build();
            }

            if (userService.emailExists(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            User user = new User(request.getEmail(), request.getPassword());
            User createdUser = userService.createUser(user);
            eventParticipantService.claimInvitationFromToken(request.getEventId(), request.getToken(), createdUser);
            LoginResponseDTO response = userService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{eventId}/{token}/decline")
    public ResponseEntity<Void> declineInvitation(@PathVariable Long eventId, @PathVariable String token) {
        try {
            eventParticipantService.rejectInvitationFromToken(eventId, token);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

