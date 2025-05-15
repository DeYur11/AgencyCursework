package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.TransactionLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionLogRepository extends MongoRepository<TransactionLog, String> {

    // Find transaction logs for a specific entity
    List<TransactionLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(AuditEntity entityType, Integer entityId);

    // Find transaction logs by audit log ID
    Optional<TransactionLog> findByAuditLogId(String auditLogId);

    // Find transaction logs by worker
    List<TransactionLog> findByWorkerIdOrderByTimestampDesc(Integer workerId);

    // Find latest non-rolled back transaction for an entity
    Optional<TransactionLog> findTopByEntityTypeAndEntityIdAndRolledBackFalseOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId);

    // Find transactions that can be rolled back (not already rolled back)
    List<TransactionLog> findByEntityTypeAndEntityIdAndRolledBackFalseOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId);

    // Find all transactions related to a project
    @Query("{'currentState.projectId': ?0}")
    List<TransactionLog> findByProjectIdOrderByTimestampDesc(Integer projectId);

    // Find transactions within a time range
    List<TransactionLog> findByEntityTypeAndEntityIdAndTimestampBetweenOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId, Instant startTime, Instant endTime);

    // Find transaction closest to but not after a specific time
    Optional<TransactionLog> findTopByEntityTypeAndEntityIdAndTimestampLessThanEqualOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId, Instant timestamp);

    // Find transactions after a specific time that haven't been rolled back
    List<TransactionLog> findByEntityTypeAndEntityIdAndTimestampGreaterThanAndRolledBackFalseOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId, Instant timestamp);

    // Find transactions by action type
    List<TransactionLog> findByEntityTypeAndEntityIdAndActionOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId, AuditAction action);

    // Count transactions by entity type and ID
    long countByEntityTypeAndEntityId(AuditEntity entityType, Integer entityId);

    // Count rolled back transactions
    long countByRolledBackTrue();

    // Find recent transactions across all entities
    List<TransactionLog> findByTimestampGreaterThanOrderByTimestampDesc(Instant timestamp);
}