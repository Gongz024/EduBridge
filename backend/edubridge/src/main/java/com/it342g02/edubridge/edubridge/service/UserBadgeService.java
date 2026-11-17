package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.UserBadge;
import com.it342g02.edubridge.edubridge.repository.UserBadgeRepository;

@Service
public class UserBadgeService {
    private final UserBadgeRepository repository;

    public UserBadgeService(UserBadgeRepository repository) {
        this.repository = repository;
    }

    // Create
    public UserBadge createUserBadge(UserBadge userBadge) {
        userBadge.setId(null);
        return repository.save(userBadge);
    }

    // Read
    public UserBadge getUserBadgeById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<UserBadge> getAllUserBadges(Long userId) {
        if (userId != null) {
            return repository.findByUser_Id(userId);
        }
        return repository.findAll();
    }

    // Update
    public UserBadge updateUserBadge(Long id, UserBadge userBadge) {
        if (repository.existsById(id)) {
            userBadge.setId(id);
            return repository.save(userBadge);
        }
        return null;
    }

    // Delete
    public boolean deleteUserBadge(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
