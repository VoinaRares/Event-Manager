package com.example.event_manager.service;

import com.example.event_manager.model.Organizer;
import com.example.event_manager.dto.LoginResponseDTO;
import com.example.event_manager.dto.OrganizerCreateDto;
import com.example.event_manager.dto.OrganizerResponseDto;
import com.example.event_manager.mapper.OrganizerMapper;
import com.example.event_manager.repository.OrganizerRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public OrganizerService(OrganizerRepository organizerRepository, JwtUtil jwtUtil) {
        this.organizerRepository = organizerRepository;
        this.jwtUtil = jwtUtil;
    }

    public OrganizerResponseDto create(OrganizerCreateDto dto) {
        if (organizerRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        String hash = passwordEncoder.encode(dto.getPassword());
        Organizer organizer = OrganizerMapper.toEntity(dto, hash);
        Organizer saved = organizerRepository.save(organizer);

        return OrganizerMapper.toDto(saved);
    }

    public Organizer findOrCreateEntity(OrganizerCreateDto dto) {
        return organizerRepository.findByEmail(dto.getEmail())
                .orElseGet(() -> {
                    String hash = passwordEncoder.encode(dto.getPassword());
                    Organizer organizer = OrganizerMapper.toEntity(dto, hash);
                    return organizerRepository.save(organizer);
                });
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

    public LoginResponseDTO login(String email, String rawPassword) {
        Organizer organizer = organizerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email is not registered"));

        if (!passwordEncoder.matches(rawPassword, organizer.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String accessToken = jwtUtil.generateAccessToken(email, organizer.getId());
        String refreshToken = jwtUtil.generateRefreshToken(email, organizer.getId());

        return new LoginResponseDTO(
                organizer.getId(),
                organizer.getEmail(),
                accessToken,
                refreshToken);
    }

    public LoginResponseDTO refreshAccessToken(String refreshToken) {
        try {
            String email = jwtUtil.extractEmail(refreshToken);
            Long userId = jwtUtil.extractUserId(refreshToken);
            String tokenType = jwtUtil.extractTokenType(refreshToken);

            if (!"refresh".equals(tokenType)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token type");
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
            }

            String newAccessToken = jwtUtil.generateAccessToken(email, userId);
            String newRefreshToken = jwtUtil.generateRefreshToken(email, userId);

            return new LoginResponseDTO(userId, email, newAccessToken, newRefreshToken);

        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token: " + e.getMessage());
        }
    }
}
