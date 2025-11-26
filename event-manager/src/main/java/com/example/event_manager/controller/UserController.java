package com.example.event_manager.controller;

import com.example.event_manager.dto.UserInvitationsResponseDto;
import com.example.event_manager.model.User;
import com.example.event_manager.service.EventParticipantService;
import com.example.event_manager.service.UserInvitationService;
import com.example.event_manager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;
    private final UserInvitationService invitationService;
    private final EventParticipantService participantService;

    public UserController(UserService userService,
                          UserInvitationService invitationService,
                          EventParticipantService participantService) {
        this.userService = userService;
        this.invitationService = invitationService;
        this.participantService = participantService;
    }

    // GET all users
    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    // GET user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lookup")
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE a new user
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        User saved = userService.createUser(user);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(saved);
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/invitations")
    public ResponseEntity<UserInvitationsResponseDto> getInvitations(@PathVariable Integer id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invitationService.getInvitations(id));
    }

    @PostMapping("/{userId}/invitations/{eventId}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable Integer userId, @PathVariable Long eventId) {
        participantService.acceptInvitation(eventId, userId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{userId}/invitations/{eventId}/decline")
    public ResponseEntity<Void> declineInvitation(@PathVariable Integer userId, @PathVariable Long eventId) {
        participantService.rejectInvitation(eventId, userId);
        return ResponseEntity.accepted().build();
    }
}
