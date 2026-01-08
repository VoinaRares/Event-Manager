package com.example.event_manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.event_manager.model.Image;
import com.example.event_manager.repository.ImageRepository;

@RestController
@RequestMapping("/api/images")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:4200")
public class ImageController {

    private final ImageRepository imageRepository;
    private final com.example.event_manager.repository.EventRepository eventRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDirConfig;

    @Value("${app.upload.base-url:http://localhost:8080/uploads/}")
    private String uploadBaseUrl;

    public ImageController(ImageRepository imageRepository, com.example.event_manager.repository.EventRepository eventRepository){
        this.imageRepository = imageRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<com.example.event_manager.dto.ImageResponseDto> getAllImages() {
        return imageRepository.findAll().stream().map(image -> {
            com.example.event_manager.dto.ImageResponseDto dto = new com.example.event_manager.dto.ImageResponseDto();
            dto.setId(image.getId());
            dto.setUrl(image.getUrl());
            dto.setPlaceholder(image.getPlaceholder());
            return dto;
        }).toList();
    }

    @PostMapping
    public com.example.event_manager.dto.ImageResponseDto createImage(@RequestBody Image image) {
        Image saved = imageRepository.save(image);
        com.example.event_manager.dto.ImageResponseDto dto = new com.example.event_manager.dto.ImageResponseDto();
        dto.setId(saved.getId());
        dto.setUrl(saved.getUrl());
        dto.setPlaceholder(saved.getPlaceholder());
        return dto;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("eventId") Long eventId,
            @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Uploaded file is not an image");
        }

        try {
            java.nio.file.Path uploadDir = java.nio.file.Paths.get(uploadDirConfig).toAbsolutePath().normalize();
            if (!java.nio.file.Files.exists(uploadDir)) {
                java.nio.file.Files.createDirectories(uploadDir);
            }

            String original = java.nio.file.Paths.get(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename()).getFileName().toString();
            String ext = "";
            int idx = original.lastIndexOf('.');
            if (idx >= 0) ext = original.substring(idx);

            String filename = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID() + ext;
            java.nio.file.Path target = uploadDir.resolve(filename);
            file.transferTo(target.toFile());

            String url = uploadBaseUrl.endsWith("/") ? uploadBaseUrl + filename : uploadBaseUrl + "/" + filename;
            com.example.event_manager.model.Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new java.lang.IllegalArgumentException("Event not found"));

            Image img = new Image();
            img.setUrl(url);
            img.setPlaceholder(null);
            img.setEvent(event);
            Image saved = imageRepository.save(img);

            com.example.event_manager.dto.ImageResponseDto dto = new com.example.event_manager.dto.ImageResponseDto();
            dto.setId(saved.getId());
            dto.setUrl(saved.getUrl());
            dto.setPlaceholder(saved.getPlaceholder());

            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Unable to save file");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.example.event_manager.dto.ImageResponseDto> getById(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(image -> {
                    com.example.event_manager.dto.ImageResponseDto dto = new com.example.event_manager.dto.ImageResponseDto();
                    dto.setId(image.getId());
                    dto.setUrl(image.getUrl());
                    dto.setPlaceholder(image.getPlaceholder());
                    return ResponseEntity.ok(dto);
                })
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
