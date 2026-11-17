package com.it342g02.edubridge.edubridge.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "grade")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_class_id", nullable = false)
    private StudentClass studentClass;

    @Column(name = "assessment_name", nullable = false)
    private String assessmentName;

    @Column(name = "score", precision = 6, scale = 2)
    private BigDecimal score;

    @Column(name = "max_score", precision = 6, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "grade_date", nullable = false)
    private LocalDate gradeDate;

    @Column(name = "remarks")
    private String remarks;

    public Grade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentClass getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(StudentClass studentClass) {
        this.studentClass = studentClass;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public LocalDate getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(LocalDate gradeDate) {
        this.gradeDate = gradeDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
