package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.Notification;
import com.it342g02.edubridge.edubridge.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    // Create
    public Notification createNotification(Notification notification) {
        notification.setId(null);
        return repository.save(notification);
    }

    // Read
    public Notification getNotificationById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Notification> getAllNotifications(Long userId) {
        if (userId != null) {
            return repository.findByUser_Id(userId);
        }
        return repository.findAll();
    }

    // Update
    public Notification updateNotification(Long id, Notification notification) {
        if (repository.existsById(id)) {
            notification.setId(id);
            return repository.save(notification);
        }
        return null;
    }

    // Delete
    public boolean deleteNotification(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
