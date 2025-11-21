package com.example.event_manager.controller;

import com.example.event_manager.dto.AdminEventCreateDto;
import com.example.event_manager.dto.AdminEventUpdateDto;
import com.example.event_manager.dto.EventResponseDto;
import com.example.event_manager.mapper.EventMapper;
import com.example.event_manager.model.Event;
import com.example.event_manager.service.AdminEventService;
import com.example.event_manager.dto.InviteParticipantRequest;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/events")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminEventController {

    private final AdminEventService adminEventService;

    public AdminEventController(AdminEventService adminEventService) {
        this.adminEventService = adminEventService;
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> create(@Valid @RequestBody AdminEventCreateDto dto) {
        Event event = adminEventService.create(dto);
        return ResponseEntity.ok(EventMapper.toDto(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> update(@PathVariable Long id, @RequestBody AdminEventUpdateDto dto) {
        Event updated = adminEventService.update(id, dto);
        return ResponseEntity.ok(EventMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminEventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<Void> invite(@PathVariable Long id, @Valid @RequestBody InviteParticipantRequest request) {
        try {
            adminEventService.inviteParticipant(id, request.getEmail());
            return ResponseEntity.accepted().build();
        } catch (ValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
