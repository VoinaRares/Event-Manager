package com.example.event_manager.mapper;

import com.example.event_manager.dto.EventParticipantDto;
import com.example.event_manager.model.EventParticipant;

public class EventParticipantMapper {
    public static EventParticipantDto toDto(EventParticipant p) {
        EventParticipantDto dto = new EventParticipantDto();
        if (p.getId() != null) {
            dto.setEventId(p.getId().getEventId());
            dto.setUserId(p.getId().getUserId());
        }
        if (p.getUser() != null) {
            dto.setUserEmail(p.getUser().getEmail());
        }
        dto.setComing(p.isComing());
        dto.setResponded(p.isResponded());
        dto.setInvitationSentAt(p.getInvitationSentAt());
        dto.setResponseAt(p.getResponseAt());
        return dto;
    }
}
