package com.example.event_manager.controller;

import com.example.event_manager.dto.OrganizerCreateDto;
import com.example.event_manager.dto.OrganizerResponseDto;
import com.example.event_manager.service.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/organizers")
@CrossOrigin(origins = "http://localhost:4200")
public class OrganizerController {

    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @PostMapping
    public ResponseEntity<OrganizerResponseDto> create(@Valid @RequestBody OrganizerCreateDto dto) {
        OrganizerResponseDto created = organizerService.create(dto);
        return ResponseEntity.created(URI.create("/api/organizers/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<OrganizerResponseDto>> listAll() {
        return ResponseEntity.ok(organizerService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(organizerService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        organizerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
