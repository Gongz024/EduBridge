package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.MissionParticipation;
import com.it342g02.edubridge.edubridge.repository.MissionParticipationRepository;

@Service
public class MissionParticipationService {
    private final MissionParticipationRepository repository;

    public MissionParticipationService(MissionParticipationRepository repository) {
        this.repository = repository;
    }

    // Create
    public MissionParticipation createMissionParticipation(MissionParticipation missionParticipation) {
        missionParticipation.setId(null);
        return repository.save(missionParticipation);
    }

    // Read
    public MissionParticipation getMissionParticipationById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<MissionParticipation> getAllMissionParticipations(Long missionId, Long parentStudentId) {
        if (missionId != null) {
            return repository.findByMission_Id(missionId);
        }
        if (parentStudentId != null) {
            return repository.findByParentStudent_Id(parentStudentId);
        }
        return repository.findAll();
    }

    // Update
    public MissionParticipation updateMissionParticipation(Long id, MissionParticipation missionParticipation) {
        if (repository.existsById(id)) {
            missionParticipation.setId(id);
            return repository.save(missionParticipation);
        }
        return null;
    }

    // Delete
    public boolean deleteMissionParticipation(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
