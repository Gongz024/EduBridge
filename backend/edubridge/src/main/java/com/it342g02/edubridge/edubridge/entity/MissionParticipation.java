package com.it342g02.edubridge.edubridge.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mission_participation")
public class MissionParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private FamilyLearningMission mission;

    @ManyToOne(optional = false)
    @JoinColumn(name = "parent_student_id", nullable = false)
    private ParentStudent parentStudent;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completion_status", nullable = false)
    private String completionStatus;

    @Column(name = "parent_confirmation")
    private Boolean parentConfirmation;

    @Column(name = "teacher_feedback", columnDefinition = "TEXT")
    private String teacherFeedback;

    public MissionParticipation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FamilyLearningMission getMission() {
        return mission;
    }

    public void setMission(FamilyLearningMission mission) {
        this.mission = mission;
    }

    public ParentStudent getParentStudent() {
        return parentStudent;
    }

    public void setParentStudent(ParentStudent parentStudent) {
        this.parentStudent = parentStudent;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public Boolean getParentConfirmation() {
        return parentConfirmation;
    }

    public void setParentConfirmation(Boolean parentConfirmation) {
        this.parentConfirmation = parentConfirmation;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }
}
