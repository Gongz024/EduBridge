package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.Badge;
import com.it342g02.edubridge.edubridge.repository.BadgeRepository;

@Service
public class BadgeService {
    private final BadgeRepository repository;

    public BadgeService(BadgeRepository repository) {
        this.repository = repository;
    }

    // Create
    public Badge createBadge(Badge badge) {
        badge.setId(null);
        return repository.save(badge);
    }

    // Read
    public Badge getBadgeById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Badge> getAllBadges() {
        return repository.findAll();
    }

    // Update
    public Badge updateBadge(Long id, Badge badge) {
        return repository.findById(id).map(existing -> {
            badge.setId(id);
            if (badge.getCreatedAt() == null) badge.setCreatedAt(existing.getCreatedAt());
            return repository.save(badge);
        }).orElse(null);
    }

    // Delete
    public boolean deleteBadge(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
