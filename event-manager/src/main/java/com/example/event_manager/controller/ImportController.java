package com.example.event_manager.controller;

import com.example.event_manager.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping("/csv")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
        return importService.importCsv(file);
    }

    @PostMapping("/excel")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        return importService.importExcel(file);
    }
}
