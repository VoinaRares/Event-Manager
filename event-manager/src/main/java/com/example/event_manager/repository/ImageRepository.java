package com.example.event_manager.repository;

import com.example.event_manager.model.Image;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>{

}
