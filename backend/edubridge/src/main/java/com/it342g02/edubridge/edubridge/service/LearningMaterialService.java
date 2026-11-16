package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.LearningMaterial;
import com.it342g02.edubridge.edubridge.repository.LearningMaterialRepository;

@Service
public class LearningMaterialService {
    private final LearningMaterialRepository repository;

    public LearningMaterialService(LearningMaterialRepository repository) {
        this.repository = repository;
    }

    // Create
    public LearningMaterial createLearningMaterial(LearningMaterial learningMaterial) {
        learningMaterial.setId(null);
        return repository.save(learningMaterial);
    }

    // Read
    public LearningMaterial getLearningMaterialById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<LearningMaterial> getAllLearningMaterials(Long lessonId) {
        if (lessonId != null) {
            return repository.findByLesson_Id(lessonId);
        }
        return repository.findAll();
    }

    // Update
    public LearningMaterial updateLearningMaterial(Long id, LearningMaterial learningMaterial) {
        return repository.findById(id).map(existing -> {
            learningMaterial.setId(id);
            if (learningMaterial.getCreatedAt() == null) learningMaterial.setCreatedAt(existing.getCreatedAt());
            return repository.save(learningMaterial);
        }).orElse(null);
    }

    // Delete
    public boolean deleteLearningMaterial(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
