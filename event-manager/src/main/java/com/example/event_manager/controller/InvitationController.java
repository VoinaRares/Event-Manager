package com.proiectcolectiv.eventmanager.controller;

import com.proiectcolectiv.eventmanager.dto.InvitationCreateDto;
import com.proiectcolectiv.eventmanager.dto.InvitationConfirmDto;
import com.proiectcolectiv.eventmanager.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService service;

    public InvitationController(InvitationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> sendInvitation(@RequestBody InvitationCreateDto dto) {
        service.sendInvitation(dto);
        return ResponseEntity.ok("Invitation sent");
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String token,
                                          @RequestBody InvitationConfirmDto dto) {
        return ResponseEntity.ok(service.confirmInvitation(token, dto));
    }
}
