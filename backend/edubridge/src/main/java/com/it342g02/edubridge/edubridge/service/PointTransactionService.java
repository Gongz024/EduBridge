package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.it342g02.edubridge.edubridge.entity.PointTransaction;
import com.it342g02.edubridge.edubridge.repository.PointTransactionRepository;

@Service
public class PointTransactionService {
    private final PointTransactionRepository repository;

    public PointTransactionService(PointTransactionRepository repository) {
        this.repository = repository;
    }

    // Create
    public PointTransaction createPointTransaction(PointTransaction pointTransaction) {
        pointTransaction.setId(null);
        return repository.save(pointTransaction);
    }

    // Read
    public PointTransaction getPointTransactionById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<PointTransaction> getAllPointTransactions(Long userId, Long missionId) {
        if (userId != null) {
            return repository.findByUser_Id(userId);
        }
        if (missionId != null) {
            return repository.findByMission_Id(missionId);
        }
        return repository.findAll();
    }

    // Update
    public PointTransaction updatePointTransaction(Long id, PointTransaction pointTransaction) {
        if (repository.existsById(id)) {
            pointTransaction.setId(id);
            return repository.save(pointTransaction);
        }
        return null;
    }

    // Delete
    public boolean deletePointTransaction(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
