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
import com.it342g02.edubridge.edubridge.entity.HomeworkSubmission;
import com.it342g02.edubridge.edubridge.service.HomeworkSubmissionService;

@RestController
@RequestMapping("/api/homework-submissions")
public class HomeworkSubmissionController {
    private final HomeworkSubmissionService service;

    public HomeworkSubmissionController(HomeworkSubmissionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<HomeworkSubmission> createHomeworkSubmission(@RequestBody HomeworkSubmission homeworkSubmission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createHomeworkSubmission(homeworkSubmission));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeworkSubmission> getHomeworkSubmissionById(@PathVariable Long id) {
        HomeworkSubmission found = service.getHomeworkSubmissionById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<HomeworkSubmission> getAllHomeworkSubmissions(@RequestParam(name = "homeworkId", required = false) Long homeworkId, @RequestParam(name = "studentId", required = false) Long studentId) {
        return service.getAllHomeworkSubmissions(homeworkId, studentId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomeworkSubmission> updateHomeworkSubmission(@PathVariable Long id, @RequestBody HomeworkSubmission homeworkSubmission) {
        HomeworkSubmission updated = service.updateHomeworkSubmission(id, homeworkSubmission);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHomeworkSubmission(@PathVariable Long id) {
        return service.deleteHomeworkSubmission(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
