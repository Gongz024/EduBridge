package com.it342g02.edubridge.edubridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.it342g02.edubridge.edubridge.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findBySchoolClass_Id(Long classId);
}
