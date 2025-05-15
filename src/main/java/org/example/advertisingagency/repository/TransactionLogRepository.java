package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.TransactionLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced repository for TransactionLog that combines the functionality
 * of both AuditLogRepository and TransactionLogRepository.
 */
public interface TransactionLogRepository extends MongoRepository<TransactionLog, String> {

    // Original TransactionLogRepository methods
    List<TransactionLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(AuditEntity entityType, Integer entityId);
    Optional<TransactionLog> findTopByEntityTypeAndEntityIdAndRolledBackFalseOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId);
    List<TransactionLog> findByEntityTypeAndEntityIdAndRolledBackFalseOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId);
    List<TransactionLog> findByTimestampGreaterThanOrderByTimestampDesc(Instant timestamp);
    List<TransactionLog> findByEntityTypeAndEntityIdAndTimestampBetweenOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId, Instant startTime, Instant endTime);
    Optional<TransactionLog> findTopByEntityTypeAndEntityIdAndTimestampLessThanEqualOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId, Instant timestamp);
    List<TransactionLog> findByEntityTypeAndEntityIdAndTimestampGreaterThanAndRolledBackFalseOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId, Instant timestamp);
    List<TransactionLog> findByEntityTypeAndEntityIdAndActionOrderByTimestampDesc(
            AuditEntity entityType, Integer entityId, AuditAction action);
    long countByEntityTypeAndEntityId(AuditEntity entityType, Integer entityId);
    long countByRolledBackTrue();

    // Former AuditLogRepository methods
    List<TransactionLog> findByWorkerId(Integer workerId);
    List<TransactionLog> findByProjectId(Integer projectId);
    List<TransactionLog> findByTaskId(Integer taskId);

    // New combined query methods with limit
    List<TransactionLog> findTop100ByProjectIdInOrderByTimestampDesc(List<Integer> projectIds);
    List<TransactionLog> findTop100ByMaterialIdInOrderByTimestampDesc(List<Integer> materialIds);

    @Query("""
    {
      "taskId": { "$in": ?1 },
      "entityType": { "$in": ?0 }
    }
    """)
    List<TransactionLog> findTop100ByEntityInAndTaskIdInOrderByTimestampAsc(
            List<AuditEntity> allowedEntities, List<Integer> taskIds);

    @Query("""
    {
      "serviceInProgressId": { "$in": ?1 },
      "entityType": { "$in": ?0 }
    }
    """)
    List<TransactionLog> findTop100ByEntityInAndServiceInProgressIdInOrderByTimestampDesc(
            List<AuditEntity> allowedEntities, List<Integer> serviceIds);

    @Query("""
    {
      "materialId": { "$in": ?1 },
      "entityType": { "$in": ?0 }
    }
    """)
    List<TransactionLog> findTop100ByEntityInAndMaterialIdInOrderByTimestampDesc(
            List<AuditEntity> allowedEntities, List<Integer> materialIds);

    @Query("""
    {
      "projectId": { "$in": ?1 },
      "entityType": { "$in": ?0 }
    }
    """)
    List<TransactionLog> findTop100ByEntityInAndProjectIdInOrderByTimestampDesc(
            List<AuditEntity> allowedEntities, List<Integer> projectIds);

    @Query("""
    {
      "materialId": { "$in": ?1 },
      "entityType": { "$eq": ?0 }
    }
    """)
    List<TransactionLog> findTop100ByEntityAndMaterialIdInOrderByTimestampDesc(
            AuditEntity entity, List<Integer> materialIds);
}