package com.example.event_manager.repository;

import com.example.event_manager.model.EventParticipant;
import com.example.event_manager.model.EventParticipantId;
import com.example.event_manager.model.Event;
import com.example.event_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
    Optional<EventParticipant> findByEventAndUser(Event event, User user);

    Optional<EventParticipant> findByEventAndToken(Event event, String token);

    List<EventParticipant> findByUser_Id(Integer userId);
}
