package com.example.event_manager.repository;

import com.example.event_manager.model.Event;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizers_Id(Long organizerId);
}
