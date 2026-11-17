package com.it342g02.edubridge.edubridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.it342g02.edubridge.edubridge.entity.StudentClass;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Long> {
    List<StudentClass> findBySchoolClass_Id(Long classId);
    List<StudentClass> findByStudent_Id(Long studentId);
}
