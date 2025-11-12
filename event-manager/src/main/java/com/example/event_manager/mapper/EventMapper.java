package com.example.event_manager.mapper;

import com.example.event_manager.dto.EventResponseDto;
import com.example.event_manager.model.Event;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {
    public static EventResponseDto toDto(Event e) {
        EventResponseDto dto = new EventResponseDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate());
        dto.setLongitude(e.getLongitude());
        dto.setLatitude(e.getLatitude());
        dto.setPlaceId(e.getPlaceId());
        dto.setLocationName(e.getLocationName());

        if (e.getOrganizers() != null) {
            dto.setOrganizers(e.getOrganizers().stream()
                    .map(OrganizerMapper::toDto)
                    .collect(Collectors.toList()));
        }

        if (e.getParticipants() != null) {
            dto.setParticipants(e.getParticipants().stream()
                    .map(EventParticipantMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static List<EventResponseDto> toDtoList(List<Event> events) {
        return events.stream().map(EventMapper::toDto).collect(Collectors.toList());
    }
}
