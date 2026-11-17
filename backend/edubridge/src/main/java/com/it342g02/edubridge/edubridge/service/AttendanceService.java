package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.Attendance;
import com.it342g02.edubridge.edubridge.repository.AttendanceRepository;

@Service
public class AttendanceService {
    private final AttendanceRepository repository;

    public AttendanceService(AttendanceRepository repository) {
        this.repository = repository;
    }

    // Create
    public Attendance createAttendance(Attendance attendance) {
        attendance.setId(null);
        return repository.save(attendance);
    }

    // Read
    public Attendance getAttendanceById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Attendance> getAllAttendance(Long studentClassId) {
        if (studentClassId != null) {
            return repository.findByStudentClass_Id(studentClassId);
        }
        return repository.findAll();
    }

    // Update
    public Attendance updateAttendance(Long id, Attendance attendance) {
        if (repository.existsById(id)) {
            attendance.setId(id);
            return repository.save(attendance);
        }
        return null;
    }

    // Delete
    public boolean deleteAttendance(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
