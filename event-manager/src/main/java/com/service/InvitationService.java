package com.proiectcolectiv.eventmanager.service;

import com.proiectcolectiv.eventmanager.dto.InvitationCreateDto;
import com.proiectcolectiv.eventmanager.dto.InvitationConfirmDto;
import com.proiectcolectiv.eventmanager.model.Invitation;
import com.proiectcolectiv.eventmanager.model.InvitationStatus;
import com.proiectcolectiv.eventmanager.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InvitationService {

    private final InvitationRepository repo;
    private final EmailService emailService;

    @Value("${app.confirmation.url}")
    private String confirmationUrl;

    public InvitationService(InvitationRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

    public void sendInvitation(InvitationCreateDto dto) {
        String token = UUID.randomUUID().toString();

        Invitation invitation = new Invitation();
        invitation.setGuestName(dto.getGuestName());
        invitation.setGuestEmail(dto.getGuestEmail());
        invitation.setToken(token);

        repo.save(invitation);

        String link = confirmationUrl + "?token=" + token;

        String html = """
                <h2>Invitație la nuntă</h2>
                <p>Dragă %s,</p>
                <p>Ai primit o invitație la un eveniment special!</p>
                <p>Te rog confirmă participarea aici:</p>
                <a href="%s">Confirmă invitația</a>
                """.formatted(dto.getGuestName(), link);

        emailService.sendHtmlEmail(dto.getGuestEmail(), "Invitație nuntă", html);
    }

    public String confirmInvitation(String token, InvitationConfirmDto dto) {
        Invitation invitation = repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (dto.isAttending()) {
            invitation.setStatus(InvitationStatus.ACCEPTED);
            invitation.setNumberOfGuests(dto.getNumberOfGuests());
        } else {
            invitation.setStatus(InvitationStatus.REJECTED);
        }

        repo.save(invitation);

        return "Confirmation saved";
    }
}
