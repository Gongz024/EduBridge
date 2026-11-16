package com.it342g02.edubridge.edubridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.it342g02.edubridge.edubridge.entity.MissionParticipation;

@Repository
public interface MissionParticipationRepository extends JpaRepository<MissionParticipation, Long> {
    List<MissionParticipation> findByMission_Id(Long missionId);
    List<MissionParticipation> findByParentStudent_Id(Long parentStudentId);
}
