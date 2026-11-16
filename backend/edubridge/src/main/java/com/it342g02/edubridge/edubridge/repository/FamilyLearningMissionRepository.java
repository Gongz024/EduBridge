package com.it342g02.edubridge.edubridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.it342g02.edubridge.edubridge.entity.FamilyLearningMission;

@Repository
public interface FamilyLearningMissionRepository extends JpaRepository<FamilyLearningMission, Long> {
    List<FamilyLearningMission> findBySchoolClass_Id(Long classId);
}
