package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.Homework;
import com.it342g02.edubridge.edubridge.repository.HomeworkRepository;

@Service
public class HomeworkService {
    private final HomeworkRepository repository;

    public HomeworkService(HomeworkRepository repository) {
        this.repository = repository;
    }

    // Create
    public Homework createHomework(Homework homework) {
        homework.setId(null);
        return repository.save(homework);
    }

    // Read
    public Homework getHomeworkById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Homework> getAllHomework(Long classId) {
        if (classId != null) {
            return repository.findBySchoolClass_Id(classId);
        }
        return repository.findAll();
    }

    // Update
    public Homework updateHomework(Long id, Homework homework) {
        return repository.findById(id).map(existing -> {
            homework.setId(id);
            if (homework.getCreatedAt() == null) homework.setCreatedAt(existing.getCreatedAt());
            return repository.save(homework);
        }).orElse(null);
    }

    // Delete
    public boolean deleteHomework(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
