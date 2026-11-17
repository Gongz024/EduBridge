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
import com.it342g02.edubridge.edubridge.entity.SchoolClass;
import com.it342g02.edubridge.edubridge.service.SchoolClassService;

@RestController
@RequestMapping("/api/classes")
public class SchoolClassController {
    private final SchoolClassService service;

    public SchoolClassController(SchoolClassService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SchoolClass> createSchoolClass(@RequestBody SchoolClass schoolClass) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSchoolClass(schoolClass));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolClass> getSchoolClassById(@PathVariable Long id) {
        SchoolClass found = service.getSchoolClassById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<SchoolClass> getAllSchoolClasses(@RequestParam(name = "teacherId", required = false) Long teacherId) {
        return service.getAllSchoolClasses(teacherId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolClass> updateSchoolClass(@PathVariable Long id, @RequestBody SchoolClass schoolClass) {
        SchoolClass updated = service.updateSchoolClass(id, schoolClass);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchoolClass(@PathVariable Long id) {
        return service.deleteSchoolClass(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
