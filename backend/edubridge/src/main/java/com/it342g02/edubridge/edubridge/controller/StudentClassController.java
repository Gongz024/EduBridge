package com.it342g02.edubridge.edubridge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.it342g02.edubridge.edubridge.entity.StudentClass;
import com.it342g02.edubridge.edubridge.service.StudentClassService;

@RestController
@RequestMapping("/api/student-classes")
public class StudentClassController {
    private final StudentClassService service;

    public StudentClassController(StudentClassService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StudentClass> createStudentClass(@RequestBody StudentClass studentClass) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStudentClass(studentClass));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentClass> getStudentClassById(@PathVariable Long id) {
        StudentClass found = service.getStudentClassById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<StudentClass> getAllStudentClasses(@RequestParam(name = "classId", required = false) Long classId, @RequestParam(name = "studentId", required = false) Long studentId) {
        return service.getAllStudentClasses(classId, studentId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentClass> updateStudentClass(@PathVariable Long id, @RequestBody StudentClass studentClass) {
        StudentClass updated = service.updateStudentClass(id, studentClass);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentClass(@PathVariable Long id) {
        return service.deleteStudentClass(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
