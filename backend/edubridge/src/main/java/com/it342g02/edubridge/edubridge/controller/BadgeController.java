package com.it342g02.edubridge.edubridge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.it342g02.edubridge.edubridge.entity.Badge;
import com.it342g02.edubridge.edubridge.service.BadgeService;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {
    private final BadgeService service;

    public BadgeController(BadgeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Badge> createBadge(@RequestBody Badge badge) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBadge(badge));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Long id) {
        Badge found = service.getBadgeById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Badge> getAllBadges() {
        return service.getAllBadges();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Badge> updateBadge(@PathVariable Long id, @RequestBody Badge badge) {
        Badge updated = service.updateBadge(id, badge);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        return service.deleteBadge(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
