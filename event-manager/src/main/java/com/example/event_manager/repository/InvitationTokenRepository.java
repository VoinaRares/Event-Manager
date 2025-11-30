package com.example.event_manager.repository;

import com.example.event_manager.model.InvitationToken;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface InvitationTokenRepository extends JpaRepository<InvitationToken, Long> {
    Optional<InvitationToken> findByToken(String token);
    
    Optional<InvitationToken> findByEventIdAndToken(Long eventId, String token);
}
