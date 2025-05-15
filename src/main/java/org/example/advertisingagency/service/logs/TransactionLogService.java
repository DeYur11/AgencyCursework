package org.example.advertisingagency.service.logs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.advertisingagency.event.RollbackEvent;
import org.example.advertisingagency.exception.RollbackException;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.TransactionLog;
import org.example.advertisingagency.publisher.TransactionPublisher;
import org.example.advertisingagency.repository.TransactionLogRepository;
import org.example.advertisingagency.service.auth.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Enhanced service for transaction logs that combines the functionality
 * of both AuditLogService and TransactionLogService.
 */
@Slf4j
@Service
public class TransactionLogService {

    private final TransactionLogRepository transactionLogRepository;
    private final TransactionPublisher transactionPublisher;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Autowired
    public TransactionLogService(
            TransactionLogRepository transactionLogRepository,
            TransactionPublisher transactionPublisher,
            ApplicationEventPublisher eventPublisher) {
        this.transactionLogRepository = transactionLogRepository;
        this.transactionPublisher = transactionPublisher;
        this.eventPublisher = eventPublisher;
        this.objectMapper = new ObjectMapper();
        // Configure object mapper with necessary modules
        objectMapper.findAndRegisterModules();
    }

    /**
     * Log a transaction with previous and current state for potential rollback.
     * This method replaces the functionality of both AuditLogService.log and
     * TransactionLogService.logTransaction.
     *
     * @param entityType The type of entity being modified
     * @param entityId The ID of the entity being modified
     * @param action The action being performed
     * @param previousState The state before the change
     * @param currentState The state after the change
     * @param description Description of the transaction
     * @param relatedIds Map of related entity IDs (project, task, etc.)
     * @return The created transaction log
     */
    @Transactional
    public TransactionLog logTransaction(
            AuditEntity entityType,
            Integer entityId,
            AuditAction action,
            Object previousState,
            Object currentState,
            String description,
            Map<String, Integer> relatedIds) {

        // Convert objects to maps for storage
        Map<String, Object> prevStateMap = convertObjectToMap(previousState);
        Map<String, Object> currStateMap = convertObjectToMap(currentState);

        // Get user context for attribution
        var user = UserContextHolder.get();

        // Create transaction log
        TransactionLog transactionLog = TransactionLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .workerId(user.getWorkerId())
                .username(user.getUsername())
                .role(user.getRole())
                .previousState(prevStateMap)
                .currentState(currStateMap)
                .projectId(relatedIds.get("projectId"))
                .serviceInProgressId(relatedIds.get("serviceInProgressId"))
                .taskId(relatedIds.get("taskId"))
                .materialId(relatedIds.get("materialId"))
                .materialReviewId(relatedIds.get("materialReviewId"))
                .timestamp(Instant.now())
                .rolledBack(false)
                .description(description)
                .build();

        // Save and publish the transaction log
        TransactionLog saved = transactionLogRepository.save(transactionLog);
        transactionPublisher.publish(saved);

        return saved;
    }

    /**
     * Rollback a specific transaction by its ID.
     *
     * @param transactionId ID of the transaction to rollback
     * @return True if rollback was successful
     */
    @Transactional
    public boolean rollbackTransaction(String transactionId) {
        Optional<TransactionLog> transactionOpt = transactionLogRepository.findById(transactionId);

        if (transactionOpt.isEmpty()) {
            throw new RollbackException("Transaction not found: " + transactionId);
        }

        TransactionLog transaction = transactionOpt.get();

        if (transaction.isRolledBack()) {
            throw new RollbackException("Transaction already rolled back: " + transactionId);
        }

        // Determine the inverse action
        AuditAction rollbackAction = getInverseAction(transaction.getAction());

        // Mark the transaction as rolled back
        transaction.setRolledBack(true);
        transactionLogRepository.save(transaction);

        // Create a rollback event
        RollbackEvent rollbackEvent = new RollbackEvent(
                this,
                transaction.getEntityType(),
                transaction.getEntityId(),
                rollbackAction,
                transaction.getPreviousState(),
                transaction.getId()
        );

        // Publish event for appropriate handlers to process
        eventPublisher.publishEvent(rollbackEvent);

        // Create a transaction log for the rollback
        var user = UserContextHolder.get();

        Map<String, Integer> relatedIds = new HashMap<>();
        if (transaction.getProjectId() != null) relatedIds.put("projectId", transaction.getProjectId());
        if (transaction.getServiceInProgressId() != null) relatedIds.put("serviceInProgressId", transaction.getServiceInProgressId());
        if (transaction.getTaskId() != null) relatedIds.put("taskId", transaction.getTaskId());
        if (transaction.getMaterialId() != null) relatedIds.put("materialId", transaction.getMaterialId());
        if (transaction.getMaterialReviewId() != null) relatedIds.put("materialReviewId", transaction.getMaterialReviewId());

        TransactionLog rollbackLog = TransactionLog.builder()
                .entityType(transaction.getEntityType())
                .entityId(transaction.getEntityId())
                .action(rollbackAction)
                .workerId(user.getWorkerId())
                .username(user.getUsername())
                .role(user.getRole())
                .previousState(transaction.getCurrentState())
                .currentState(transaction.getPreviousState())
                .projectId(transaction.getProjectId())
                .serviceInProgressId(transaction.getServiceInProgressId())
                .taskId(transaction.getTaskId())
                .materialId(transaction.getMaterialId())
                .materialReviewId(transaction.getMaterialReviewId())
                .timestamp(Instant.now())
                .rolledBack(false)
                .rollbackTransactionId(transaction.getId())
                .description("ROLLBACK: " + transaction.getDescription())
                .build();

        TransactionLog savedRollbackLog = transactionLogRepository.save(rollbackLog);
        transactionPublisher.publish(savedRollbackLog);

        return true;
    }

    /**
     * Get transaction logs for task IDs.
     */
    public List<TransactionLog> findByTaskIds(List<AuditEntity> entityList, List<Integer> taskIds) {
        return transactionLogRepository.findTop100ByEntityInAndTaskIdInOrderByTimestampAsc(entityList, taskIds);
    }

    /**
     * Get transaction logs for material IDs.
     */
    public List<TransactionLog> findByMaterialIds(List<AuditEntity> entityList, List<Integer> materialIds) {
        return transactionLogRepository.findTop100ByEntityInAndMaterialIdInOrderByTimestampDesc(entityList, materialIds);
    }

    /**
     * Get transaction logs for project IDs.
     */
    public List<TransactionLog> findByProjectIds(List<AuditEntity> entityList, List<Integer> projectIds) {
        return transactionLogRepository.findTop100ByEntityInAndProjectIdInOrderByTimestampDesc(entityList, projectIds);
    }

    /**
     * Get transaction logs for service in progress IDs.
     */
    public List<TransactionLog> findByServiceInProgressIds(List<AuditEntity> entityList, List<Integer> serviceIds) {
        return transactionLogRepository.findTop100ByEntityInAndServiceInProgressIdInOrderByTimestampDesc(entityList, serviceIds);
    }

    /**
     * Restore an entity to a specific point in time.
     */
    @Transactional
    public String restoreEntityToPoint(AuditEntity entityType, Integer entityId, String timestamp) {
        Instant targetTime = Instant.parse(timestamp);

        // Find the transaction closest to but not after the target time
        Optional<TransactionLog> transactionOpt = transactionLogRepository.findTopByEntityTypeAndEntityIdAndTimestampLessThanEqualOrderByTimestampDesc(
                entityType, entityId, targetTime);

        if (transactionOpt.isEmpty()) {
            throw new RollbackException("No transaction found before or at the target time");
        }

        TransactionLog transaction = transactionOpt.get();

        // Check if already rolled back
        if (transaction.isRolledBack()) {
            throw new RollbackException("Target transaction is already rolled back");
        }

        // Rollback all transactions after the target time
        List<TransactionLog> laterTransactions = transactionLogRepository.findByEntityTypeAndEntityIdAndTimestampGreaterThanAndRolledBackFalseOrderByTimestampDesc(
                entityType, entityId, targetTime);

        // Rollback from newest to oldest
        String lastRollbackId = null;
        for (TransactionLog tx : laterTransactions) {
            boolean success = rollbackTransaction(tx.getId());
            if (success) {
                lastRollbackId = tx.getId();
            } else {
                throw new RollbackException("Failed to rollback transaction: " + tx.getId());
            }
        }

        return lastRollbackId;
    }

    /**
     * Get transactions from the last N hours for an entity.
     */
    public List<TransactionLog> getRecentTransactions(AuditEntity entityType, Integer entityId, int hours) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(hours, ChronoUnit.HOURS);

        return transactionLogRepository.findByEntityTypeAndEntityIdAndTimestampBetweenOrderByTimestampDesc(
                entityType, entityId, startTime, endTime);
    }

    /**
     * Get the inverse action for rollback purposes.
     */
    private AuditAction getInverseAction(AuditAction action) {
        return switch (action) {
            case CREATE -> AuditAction.DELETE;
            case DELETE -> AuditAction.CREATE;
            case UPDATE -> AuditAction.UPDATE; // For update, the inverse is another update
            case ROLLBACK -> AuditAction.ROLLBACK;
        };
    }

    /**
     * Convert a Java object to a Map for storage.
     */
    private Map<String, Object> convertObjectToMap(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }

        try {
            return objectMapper.convertValue(obj, Map.class);
        } catch (Exception e) {
            log.error("Error converting object to map", e);
            return new HashMap<>();
        }
    }
}