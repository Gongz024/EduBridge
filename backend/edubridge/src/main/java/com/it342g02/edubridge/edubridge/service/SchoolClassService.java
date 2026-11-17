package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.SchoolClass;
import com.it342g02.edubridge.edubridge.repository.SchoolClassRepository;

@Service
public class SchoolClassService {
    private final SchoolClassRepository repository;

    public SchoolClassService(SchoolClassRepository repository) {
        this.repository = repository;
    }

    // Create
    public SchoolClass createSchoolClass(SchoolClass schoolClass) {
        schoolClass.setId(null);
        return repository.save(schoolClass);
    }

    // Read
    public SchoolClass getSchoolClassById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<SchoolClass> getAllSchoolClasses(Long teacherId) {
        if (teacherId != null) {
            return repository.findByTeacher_Id(teacherId);
        }
        return repository.findAll();
    }

    // Update
    public SchoolClass updateSchoolClass(Long id, SchoolClass schoolClass) {
        return repository.findById(id).map(existing -> {
            schoolClass.setId(id);
            if (schoolClass.getCreatedAt() == null) schoolClass.setCreatedAt(existing.getCreatedAt());
            return repository.save(schoolClass);
        }).orElse(null);
    }

    // Delete
    public boolean deleteSchoolClass(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
