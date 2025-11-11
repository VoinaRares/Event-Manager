package com.example.event_manager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.event_manager.model.Image;
import com.example.event_manager.repository.ImageRepository;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageRepository imageRepository;

    public ImageController(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    @GetMapping
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @PostMapping
    public Image createImage(@RequestBody Image image) {
        return imageRepository.save(image);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getById(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        if (!imageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        imageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
}
