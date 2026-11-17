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
import com.it342g02.edubridge.edubridge.entity.ParentStudent;
import com.it342g02.edubridge.edubridge.service.ParentStudentService;

@RestController
@RequestMapping("/api/parent-students")
public class ParentStudentController {
    private final ParentStudentService service;

    public ParentStudentController(ParentStudentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ParentStudent> createParentStudent(@RequestBody ParentStudent parentStudent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createParentStudent(parentStudent));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParentStudent> getParentStudentById(@PathVariable Long id) {
        ParentStudent found = service.getParentStudentById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<ParentStudent> getAllParentStudents(@RequestParam(name = "parentId", required = false) Long parentId, @RequestParam(name = "studentId", required = false) Long studentId) {
        return service.getAllParentStudents(parentId, studentId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParentStudent> updateParentStudent(@PathVariable Long id, @RequestBody ParentStudent parentStudent) {
        ParentStudent updated = service.updateParentStudent(id, parentStudent);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParentStudent(@PathVariable Long id) {
        return service.deleteParentStudent(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
