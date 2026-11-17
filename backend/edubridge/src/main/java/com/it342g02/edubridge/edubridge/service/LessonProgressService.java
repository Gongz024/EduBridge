package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.LessonProgress;
import com.it342g02.edubridge.edubridge.repository.LessonProgressRepository;

@Service
public class LessonProgressService {
    private final LessonProgressRepository repository;

    public LessonProgressService(LessonProgressRepository repository) {
        this.repository = repository;
    }

    // Create
    public LessonProgress createLessonProgress(LessonProgress lessonProgress) {
        lessonProgress.setId(null);
        return repository.save(lessonProgress);
    }

    // Read
    public LessonProgress getLessonProgressById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<LessonProgress> getAllLessonProgress(Long lessonId, Long studentId) {
        if (lessonId != null) {
            return repository.findByLesson_Id(lessonId);
        }
        if (studentId != null) {
            return repository.findByStudent_Id(studentId);
        }
        return repository.findAll();
    }

    // Update
    public LessonProgress updateLessonProgress(Long id, LessonProgress lessonProgress) {
        if (repository.existsById(id)) {
            lessonProgress.setId(id);
            return repository.save(lessonProgress);
        }
        return null;
    }

    // Delete
    public boolean deleteLessonProgress(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
