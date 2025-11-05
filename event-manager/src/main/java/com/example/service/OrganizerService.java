package com.proiectcolectiv.eventmanager.service;

import com.proiectcolectiv.eventmanager.model.Organizer;
import com.proiectcolectiv.eventmanager.dto.OrganizerCreateDto;
import com.proiectcolectiv.eventmanager.dto.OrganizerResponseDto;
import com.proiectcolectiv.eventmanager.mapper.OrganizerMapper;
import com.proiectcolectiv.eventmanager.repository.OrganizerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public OrganizerService(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    public OrganizerResponseDto create(OrganizerCreateDto dto) {
        if (organizerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        String hash = passwordEncoder.encode(dto.getPassword());
        Organizer organizer = OrganizerMapper.toEntity(dto, hash);
        Organizer saved = organizerRepository.save(organizer);

        return OrganizerMapper.toDto(saved);
    }

    public List<OrganizerResponseDto> listAll() {
        return organizerRepository.findAll().stream()
                .map(OrganizerMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrganizerResponseDto getById(Long id) {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found"));
        return OrganizerMapper.toDto(organizer);
    }

    public void delete(Long id) {
        organizerRepository.deleteById(id);
    }
}
