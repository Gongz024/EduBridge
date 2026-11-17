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
import com.it342g02.edubridge.edubridge.entity.LessonProgress;
import com.it342g02.edubridge.edubridge.service.LessonProgressService;

@RestController
@RequestMapping("/api/lesson-progress")
public class LessonProgressController {
    private final LessonProgressService service;

    public LessonProgressController(LessonProgressService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LessonProgress> createLessonProgress(@RequestBody LessonProgress lessonProgress) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLessonProgress(lessonProgress));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonProgress> getLessonProgressById(@PathVariable Long id) {
        LessonProgress found = service.getLessonProgressById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<LessonProgress> getAllLessonProgress(@RequestParam(name = "lessonId", required = false) Long lessonId, @RequestParam(name = "studentId", required = false) Long studentId) {
        return service.getAllLessonProgress(lessonId, studentId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonProgress> updateLessonProgress(@PathVariable Long id, @RequestBody LessonProgress lessonProgress) {
        LessonProgress updated = service.updateLessonProgress(id, lessonProgress);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLessonProgress(@PathVariable Long id) {
        return service.deleteLessonProgress(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
