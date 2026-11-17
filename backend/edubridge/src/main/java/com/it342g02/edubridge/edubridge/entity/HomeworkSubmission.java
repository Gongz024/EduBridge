package com.it342g02.edubridge.edubridge.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "homework_submission")
public class HomeworkSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "submission_content", columnDefinition = "TEXT")
    private String submissionContent;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "score", precision = 6, scale = 2)
    private BigDecimal score;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    public HomeworkSubmission() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Homework getHomework() {
        return homework;
    }

    public void setHomework(Homework homework) {
        this.homework = homework;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public String getSubmissionContent() {
        return submissionContent;
    }

    public void setSubmissionContent(String submissionContent) {
        this.submissionContent = submissionContent;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
