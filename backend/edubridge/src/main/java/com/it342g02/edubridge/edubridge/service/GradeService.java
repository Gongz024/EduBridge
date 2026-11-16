package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.Grade;
import com.it342g02.edubridge.edubridge.repository.GradeRepository;

@Service
public class GradeService {
    private final GradeRepository repository;

    public GradeService(GradeRepository repository) {
        this.repository = repository;
    }

    // Create
    public Grade createGrade(Grade grade) {
        grade.setId(null);
        return repository.save(grade);
    }

    // Read
    public Grade getGradeById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Grade> getAllGrades(Long studentClassId) {
        if (studentClassId != null) {
            return repository.findByStudentClass_Id(studentClassId);
        }
        return repository.findAll();
    }

    // Update
    public Grade updateGrade(Long id, Grade grade) {
        if (repository.existsById(id)) {
            grade.setId(id);
            return repository.save(grade);
        }
        return null;
    }

    // Delete
    public boolean deleteGrade(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
