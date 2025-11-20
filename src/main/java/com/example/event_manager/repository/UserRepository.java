package com.example.event_manager.repository;

import com.example.event_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String q);

    // Custom query to fetch User with events eagerly loaded by ID
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.events WHERE u.id = :id")
    Optional<User> findByIdWithEvents(@Param("id") Long id);
}
