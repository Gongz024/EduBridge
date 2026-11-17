package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.Lesson;
import com.it342g02.edubridge.edubridge.repository.LessonRepository;

@Service
public class LessonService {
    private final LessonRepository repository;

    public LessonService(LessonRepository repository) {
        this.repository = repository;
    }

    // Create
    public Lesson createLesson(Lesson lesson) {
        lesson.setId(null);
        return repository.save(lesson);
    }

    // Read
    public Lesson getLessonById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Lesson> getAllLessons(Long classId) {
        if (classId != null) {
            return repository.findBySchoolClass_Id(classId);
        }
        return repository.findAll();
    }

    // Update
    public Lesson updateLesson(Long id, Lesson lesson) {
        if (repository.existsById(id)) {
            lesson.setId(id);
            return repository.save(lesson);
        }
        return null;
    }

    // Delete
    public boolean deleteLesson(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
