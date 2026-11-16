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
import com.it342g02.edubridge.edubridge.entity.FamilyLearningMission;
import com.it342g02.edubridge.edubridge.service.FamilyLearningMissionService;

@RestController
@RequestMapping("/api/family-missions")
public class FamilyLearningMissionController {
    private final FamilyLearningMissionService service;

    public FamilyLearningMissionController(FamilyLearningMissionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FamilyLearningMission> createFamilyLearningMission(@RequestBody FamilyLearningMission familyLearningMission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFamilyLearningMission(familyLearningMission));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyLearningMission> getFamilyLearningMissionById(@PathVariable Long id) {
        FamilyLearningMission found = service.getFamilyLearningMissionById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<FamilyLearningMission> getAllFamilyLearningMissions(@RequestParam(name = "classId", required = false) Long classId) {
        return service.getAllFamilyLearningMissions(classId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamilyLearningMission> updateFamilyLearningMission(@PathVariable Long id, @RequestBody FamilyLearningMission familyLearningMission) {
        FamilyLearningMission updated = service.updateFamilyLearningMission(id, familyLearningMission);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFamilyLearningMission(@PathVariable Long id) {
        return service.deleteFamilyLearningMission(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
