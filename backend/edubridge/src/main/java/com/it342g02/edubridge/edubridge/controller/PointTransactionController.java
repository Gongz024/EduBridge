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
import com.it342g02.edubridge.edubridge.entity.PointTransaction;
import com.it342g02.edubridge.edubridge.service.PointTransactionService;

@RestController
@RequestMapping("/api/point-transactions")
public class PointTransactionController {
    private final PointTransactionService service;

    public PointTransactionController(PointTransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PointTransaction> createPointTransaction(@RequestBody PointTransaction pointTransaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPointTransaction(pointTransaction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointTransaction> getPointTransactionById(@PathVariable Long id) {
        PointTransaction found = service.getPointTransactionById(id);
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<PointTransaction> getAllPointTransactions(@RequestParam(name = "userId", required = false) Long userId, @RequestParam(name = "missionId", required = false) Long missionId) {
        return service.getAllPointTransactions(userId, missionId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointTransaction> updatePointTransaction(@PathVariable Long id, @RequestBody PointTransaction pointTransaction) {
        PointTransaction updated = service.updatePointTransaction(id, pointTransaction);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePointTransaction(@PathVariable Long id) {
        return service.deletePointTransaction(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
