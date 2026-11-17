package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.Announcement;
import com.it342g02.edubridge.edubridge.repository.AnnouncementRepository;

@Service
public class AnnouncementService {
    private final AnnouncementRepository repository;

    public AnnouncementService(AnnouncementRepository repository) {
        this.repository = repository;
    }

    // Create
    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setId(null);
        return repository.save(announcement);
    }

    // Read
    public Announcement getAnnouncementById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Announcement> getAllAnnouncements(Long classId) {
        if (classId != null) {
            return repository.findBySchoolClass_Id(classId);
        }
        return repository.findAll();
    }

    // Update
    public Announcement updateAnnouncement(Long id, Announcement announcement) {
        return repository.findById(id).map(existing -> {
            announcement.setId(id);
            if (announcement.getCreatedAt() == null) announcement.setCreatedAt(existing.getCreatedAt());
            return repository.save(announcement);
        }).orElse(null);
    }

    // Delete
    public boolean deleteAnnouncement(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
