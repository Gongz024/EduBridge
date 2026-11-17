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
import com.it342g02.edubridge.edubridge.entity.MissionParticipation;
import com.it342g02.edubridge.edubridge.service.MissionParticipationService;

@RestController
@RequestMapping("/api/mission-participations")
public class MissionParticipationController {
    private final MissionParticipationService service;

    public MissionParticipationController(MissionParticipationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MissionParticipation> createMissionParticipation(@RequestBody MissionParticipation missionParticipation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMissionParticipation(missionParticipation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionParticipation> getMissionParticipationById(@PathVariable Long id) {
        MissionParticipation found = service.getMissionParticipationById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<MissionParticipation> getAllMissionParticipations(@RequestParam(name = "missionId", required = false) Long missionId, @RequestParam(name = "parentStudentId", required = false) Long parentStudentId) {
        return service.getAllMissionParticipations(missionId, parentStudentId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionParticipation> updateMissionParticipation(@PathVariable Long id, @RequestBody MissionParticipation missionParticipation) {
        MissionParticipation updated = service.updateMissionParticipation(id, missionParticipation);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMissionParticipation(@PathVariable Long id) {
        return service.deleteMissionParticipation(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
