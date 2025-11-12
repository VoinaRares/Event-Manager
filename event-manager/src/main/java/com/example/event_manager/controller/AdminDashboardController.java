package com.example.event_manager.controller;

import com.example.event_manager.dto.AdminHomeResponseDto;
import com.example.event_manager.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/home")
    public ResponseEntity<AdminHomeResponseDto> getHome(@RequestParam Long organizerId) {
        return ResponseEntity.ok(dashboardService.getHomeData(organizerId));
    }
}

