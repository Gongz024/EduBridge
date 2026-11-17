package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.ParentStudent;
import com.it342g02.edubridge.edubridge.repository.ParentStudentRepository;

@Service
public class ParentStudentService {
    private final ParentStudentRepository repository;

    public ParentStudentService(ParentStudentRepository repository) {
        this.repository = repository;
    }

    // Create
    public ParentStudent createParentStudent(ParentStudent parentStudent) {
        parentStudent.setId(null);
        return repository.save(parentStudent);
    }

    // Read
    public ParentStudent getParentStudentById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<ParentStudent> getAllParentStudents(Long parentId, Long studentId) {
        if (parentId != null) {
            return repository.findByParent_Id(parentId);
        }
        if (studentId != null) {
            return repository.findByStudent_Id(studentId);
        }
        return repository.findAll();
    }

    // Update
    public ParentStudent updateParentStudent(Long id, ParentStudent parentStudent) {
        if (repository.existsById(id)) {
            parentStudent.setId(id);
            return repository.save(parentStudent);
        }
        return null;
    }

    // Delete
    public boolean deleteParentStudent(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
