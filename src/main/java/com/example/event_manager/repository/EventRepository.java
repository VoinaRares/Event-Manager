package com.example.event_manager.repository;

import com.example.event_manager.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    // For search functionality
    List<Event> findByTitleContainingIgnoreCase(String keyword);
}
