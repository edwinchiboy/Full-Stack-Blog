package com.blog.cutom_blog.controllers;

import com.blog.cutom_blog.services.DashboardStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired
    private DashboardStatsService dashboardStatsService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = dashboardStatsService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/posts")
    public ResponseEntity<Map<String, Long>> getPostStats() {
        Map<String, Long> stats = dashboardStatsService.getPostStatsByStatus();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/subscribers")
    public ResponseEntity<Map<String, Long>> getSubscriberStats() {
        Map<String, Long> stats = dashboardStatsService.getSubscriberStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/engagement")
    public ResponseEntity<Map<String, Object>> getEngagementStats() {
        Map<String, Object> stats = dashboardStatsService.getEngagementStats();
        return ResponseEntity.ok(stats);
    }
}