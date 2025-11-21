package com.example.event_manager.mapper;

import com.example.event_manager.model.Organizer;
import com.example.event_manager.dto.OrganizerCreateDto;
import com.example.event_manager.dto.OrganizerResponseDto;

public class OrganizerMapper {
    public static Organizer toEntity(OrganizerCreateDto d, String passwordHash) {
        Organizer o = new Organizer();
        o.setName(d.getName());
        o.setEmail(d.getEmail());
        o.setPasswordHash(passwordHash);
        o.setPhone(d.getPhone());
        return o;
    }

    public static OrganizerResponseDto toDto(Organizer o) {
        OrganizerResponseDto dto = new OrganizerResponseDto();
        dto.setId(o.getId());
        dto.setName(o.getName());
        dto.setEmail(o.getEmail());
        dto.setPhone(o.getPhone());
        dto.setCreatedAt(o.getCreatedAt());
        return dto;
    }
}
