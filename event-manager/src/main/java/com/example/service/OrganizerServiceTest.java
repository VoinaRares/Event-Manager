package com.example.eventmanager.service;

import com.example.eventmanager.domain.Organizer;
import com.example.eventmanager.dto.OrganizerCreateDto;
import com.example.eventmanager.repository.OrganizerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizerServiceTest {
    @Test
    void createAndGet() {
        OrganizerRepository repo = mock(OrganizerRepository.class);
        OrganizerService service = new OrganizerService(repo);

        OrganizerCreateDto dto = new OrganizerCreateDto();
        dto.setName("Test");
        dto.setEmail("a@b.c");
        dto.setPassword("pass");

        when(repo.existsByEmail(dto.getEmail())).thenReturn(false);
        Organizer saved = new Organizer(dto.getName(), dto.getEmail(), "hash");
        saved.setId(1L);
        when(repo.save(any(Organizer.class))).thenReturn(saved);

        var resp = service.create(dto);
        assertEquals(saved.getId(), resp.getId());
    }
}
