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
import com.it342g02.edubridge.edubridge.entity.Homework;
import com.it342g02.edubridge.edubridge.service.HomeworkService;

@RestController
@RequestMapping("/api/homework")
public class HomeworkController {
    private final HomeworkService service;

    public HomeworkController(HomeworkService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Homework> createHomework(@RequestBody Homework homework) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createHomework(homework));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Homework> getHomeworkById(@PathVariable Long id) {
        Homework found = service.getHomeworkById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Homework> getAllHomework(@RequestParam(name = "classId", required = false) Long classId) {
        return service.getAllHomework(classId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Homework> updateHomework(@PathVariable Long id, @RequestBody Homework homework) {
        Homework updated = service.updateHomework(id, homework);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHomework(@PathVariable Long id) {
        return service.deleteHomework(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
