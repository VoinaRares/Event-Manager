package com.example.eventmanager.repository;

import com.example.eventmanager.domain.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
    Optional<Organizer> findByEmail(String email);
    boolean existsByEmail(String email);
}

