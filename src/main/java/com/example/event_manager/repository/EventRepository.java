package com.example.event_manager.repository;

import com.example.event_manager.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    // For search functionality
    List<Event> findByTitleContainingIgnoreCase(String keyword);

    // For "View My Events" feature: find all events by user id
    List<Event> findByUserId(Long userId);

    // Alternatively, to use the object reference:
    // List<Event> findByUser(User user);
}
