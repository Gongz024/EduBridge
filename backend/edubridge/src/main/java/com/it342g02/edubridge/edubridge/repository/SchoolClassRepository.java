package com.it342g02.edubridge.edubridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.it342g02.edubridge.edubridge.entity.SchoolClass;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findByTeacher_Id(Long teacherId);
}
