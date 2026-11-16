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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.it342g02.edubridge.edubridge.entity.UserBadge;
import com.it342g02.edubridge.edubridge.service.UserBadgeService;

@RestController
@RequestMapping("/api/user-badges")
public class UserBadgeController {
    private final UserBadgeService service;

    public UserBadgeController(UserBadgeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserBadge> createUserBadge(@RequestBody UserBadge userBadge) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUserBadge(userBadge));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBadge> getUserBadgeById(@PathVariable Long id) {
        UserBadge found = service.getUserBadgeById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<UserBadge> getAllUserBadges(@RequestParam(name = "userId", required = false) Long userId) {
        return service.getAllUserBadges(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserBadge> updateUserBadge(@PathVariable Long id, @RequestBody UserBadge userBadge) {
        UserBadge updated = service.updateUserBadge(id, userBadge);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserBadge(@PathVariable Long id) {
        return service.deleteUserBadge(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
