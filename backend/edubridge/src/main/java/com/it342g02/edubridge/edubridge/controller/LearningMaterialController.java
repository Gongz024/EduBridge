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
import com.it342g02.edubridge.edubridge.entity.LearningMaterial;
import com.it342g02.edubridge.edubridge.service.LearningMaterialService;

@RestController
@RequestMapping("/api/learning-materials")
public class LearningMaterialController {
    private final LearningMaterialService service;

    public LearningMaterialController(LearningMaterialService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LearningMaterial> createLearningMaterial(@RequestBody LearningMaterial learningMaterial) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLearningMaterial(learningMaterial));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningMaterial> getLearningMaterialById(@PathVariable Long id) {
        LearningMaterial found = service.getLearningMaterialById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<LearningMaterial> getAllLearningMaterials(@RequestParam(name = "lessonId", required = false) Long lessonId) {
        return service.getAllLearningMaterials(lessonId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningMaterial> updateLearningMaterial(@PathVariable Long id, @RequestBody LearningMaterial learningMaterial) {
        LearningMaterial updated = service.updateLearningMaterial(id, learningMaterial);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningMaterial(@PathVariable Long id) {
        return service.deleteLearningMaterial(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
