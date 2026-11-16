package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.FamilyLearningMission;
import com.it342g02.edubridge.edubridge.repository.FamilyLearningMissionRepository;

@Service
public class FamilyLearningMissionService {
    private final FamilyLearningMissionRepository repository;

    public FamilyLearningMissionService(FamilyLearningMissionRepository repository) {
        this.repository = repository;
    }

    // Create
    public FamilyLearningMission createFamilyLearningMission(FamilyLearningMission familyLearningMission) {
        familyLearningMission.setId(null);
        return repository.save(familyLearningMission);
    }

    // Read
    public FamilyLearningMission getFamilyLearningMissionById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<FamilyLearningMission> getAllFamilyLearningMissions(Long classId) {
        if (classId != null) {
            return repository.findBySchoolClass_Id(classId);
        }
        return repository.findAll();
    }

    // Update
    public FamilyLearningMission updateFamilyLearningMission(Long id, FamilyLearningMission familyLearningMission) {
        return repository.findById(id).map(existing -> {
            familyLearningMission.setId(id);
            if (familyLearningMission.getCreatedAt() == null) familyLearningMission.setCreatedAt(existing.getCreatedAt());
            return repository.save(familyLearningMission);
        }).orElse(null);
    }

    // Delete
    public boolean deleteFamilyLearningMission(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
