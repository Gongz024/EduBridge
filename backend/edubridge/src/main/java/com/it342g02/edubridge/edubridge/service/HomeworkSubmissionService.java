package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.HomeworkSubmission;
import com.it342g02.edubridge.edubridge.repository.HomeworkSubmissionRepository;

@Service
public class HomeworkSubmissionService {
    private final HomeworkSubmissionRepository repository;

    public HomeworkSubmissionService(HomeworkSubmissionRepository repository) {
        this.repository = repository;
    }

    // Create
    public HomeworkSubmission createHomeworkSubmission(HomeworkSubmission homeworkSubmission) {
        homeworkSubmission.setId(null);
        return repository.save(homeworkSubmission);
    }

    // Read
    public HomeworkSubmission getHomeworkSubmissionById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<HomeworkSubmission> getAllHomeworkSubmissions(Long homeworkId, Long studentId) {
        if (homeworkId != null) {
            return repository.findByHomework_Id(homeworkId);
        }
        if (studentId != null) {
            return repository.findByStudent_Id(studentId);
        }
        return repository.findAll();
    }

    // Update
    public HomeworkSubmission updateHomeworkSubmission(Long id, HomeworkSubmission homeworkSubmission) {
        if (repository.existsById(id)) {
            homeworkSubmission.setId(id);
            return repository.save(homeworkSubmission);
        }
        return null;
    }

    // Delete
    public boolean deleteHomeworkSubmission(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
