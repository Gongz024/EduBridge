package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.StudentClass;
import com.it342g02.edubridge.edubridge.repository.StudentClassRepository;

@Service
public class StudentClassService {
    private final StudentClassRepository repository;

    public StudentClassService(StudentClassRepository repository) {
        this.repository = repository;
    }

    // Create
    public StudentClass createStudentClass(StudentClass studentClass) {
        studentClass.setId(null);
        return repository.save(studentClass);
    }

    // Read
    public StudentClass getStudentClassById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<StudentClass> getAllStudentClasses(Long classId, Long studentId) {
        if (classId != null) {
            return repository.findBySchoolClass_Id(classId);
        }
        if (studentId != null) {
            return repository.findByStudent_Id(studentId);
        }
        return repository.findAll();
    }

    // Update
    public StudentClass updateStudentClass(Long id, StudentClass studentClass) {
        if (repository.existsById(id)) {
            studentClass.setId(id);
            return repository.save(studentClass);
        }
        return null;
    }

    // Delete
    public boolean deleteStudentClass(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
