package com.example.event_manager.service;

import com.example.event_manager.dto.UserInvitationDto;
import com.example.event_manager.dto.UserInvitationsResponseDto;
import com.example.event_manager.model.EventParticipant;
import com.example.event_manager.repository.EventParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserInvitationService {

    private final EventParticipantRepository participantRepository;

    public UserInvitationService(EventParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public UserInvitationsResponseDto getInvitations(Integer userId) {
        List<EventParticipant> participants = participantRepository.findByUser_Id(userId);

        List<UserInvitationDto> confirmed = participants.stream()
                .filter(EventParticipant::isComing)
                .map(this::toDto)
                .collect(Collectors.toList());

        List<UserInvitationDto> pending = participants.stream()
                .filter(p -> !p.isResponded())
                .map(this::toDto)
                .collect(Collectors.toList());

        UserInvitationsResponseDto response = new UserInvitationsResponseDto();
        response.setPending(pending);
        response.setConfirmed(confirmed);
        return response;
    }

    private UserInvitationDto toDto(EventParticipant participant) {
        UserInvitationDto dto = new UserInvitationDto();
        dto.setEventId(participant.getEvent().getId());
        dto.setName(participant.getEvent().getName());
        dto.setStartDate(participant.getEvent().getStartDate());
        dto.setEndDate(participant.getEvent().getEndDate());
        dto.setLocationName(participant.getEvent().getLocationName());
        return dto;
    }
}
