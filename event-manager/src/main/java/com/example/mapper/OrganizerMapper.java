package com.example.eventmanager.mapper;

import com.example.eventmanager.domain.Organizer;
import com.example.eventmanager.dto.OrganizerCreateDto;
import com.example.eventmanager.dto.OrganizerResponseDto;

public class OrganizerMapper {
    public static Organizer toEntity(OrganizerCreateDto d, String passwordHash) {
        Organizer o = new Organizer();
        o.setName(d.getName());
        o.setEmail(d.getEmail());
        o.setPasswordHash(passwordHash);
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
