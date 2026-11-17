package com.it342g02.edubridge.edubridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.it342g02.edubridge.edubridge.entity.LessonProgress;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByLesson_Id(Long lessonId);
    List<LessonProgress> findByStudent_Id(Long studentId);
}
