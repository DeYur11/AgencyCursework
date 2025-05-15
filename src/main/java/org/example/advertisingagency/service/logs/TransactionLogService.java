package org.example.advertisingagency.service.logs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.advertisingagency.event.RollbackEvent;
import org.example.advertisingagency.exception.RollbackException;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.example.advertisingagency.model.log.TransactionLog;
import org.example.advertisingagency.repository.AuditLogRepository;
import org.example.advertisingagency.repository.TransactionLogRepository;
import org.example.advertisingagency.service.auth.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for handling transaction logs and rollback operations.
 */
@Slf4j
@Service
public class TransactionLogService {

    private final TransactionLogRepository transactionLogRepository;
    private final AuditLogPublisher auditLogPublisher;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Autowired
    public TransactionLogService(
            TransactionLogRepository transactionLogRepository,
            AuditLogPublisher auditLogPublisher,
            AuditLogRepository auditLogRepository,
            ApplicationEventPublisher eventPublisher) {
        this.transactionLogRepository = transactionLogRepository;
        this.auditLogPublisher = auditLogPublisher;
        this.auditLogRepository = auditLogRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = new ObjectMapper();
        // Configure object mapper with necessary modules
        objectMapper.findAndRegisterModules();
    }

    /**
     * Log a transaction with previous and current state for potential rollback.
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

        // Create and save audit log
        var user = UserContextHolder.get();
        AuditLog auditLog = AuditLog.builder()
                .workerId(user.getWorkerId())
                .username(user.getUsername())
                .role(user.getRole())
                .action(action)
                .entity(entityType)
                .description(description)
                .projectId(relatedIds.get("projectId"))
                .serviceInProgressId(relatedIds.get("serviceInProgressId"))
                .taskId(relatedIds.get("taskId"))
                .materialId(relatedIds.get("materialId"))
                .materialReviewId(relatedIds.get("materialReviewId"))
                .timestamp(Instant.now())
                .build();

        AuditLog savedAuditLog = auditLogRepository.save(auditLog);
        auditLogPublisher.publish(savedAuditLog);

        // Create transaction log
        TransactionLog transactionLog = TransactionLog.builder()
                .auditLogId(savedAuditLog.getId())
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .workerId(user.getWorkerId())
                .username(user.getUsername())
                .previousState(prevStateMap)
                .currentState(currStateMap)
                .timestamp(Instant.now())
                .rolledBack(false)
                .build();

        return transactionLogRepository.save(transactionLog);
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

        // Create an audit log for the rollback
        var user = UserContextHolder.get();
        AuditLog rollbackAuditLog = AuditLog.builder()
                .workerId(user.getWorkerId())
                .username(user.getUsername())
                .role(user.getRole())
                .action(rollbackAction)
                .entity(transaction.getEntityType())
                .description("ROLLBACK: " + transaction.getEntityType() + " ID: " + transaction.getEntityId())
                .projectId(getIntegerFromMap(transaction.getCurrentState(), "projectId"))
                .serviceInProgressId(getIntegerFromMap(transaction.getCurrentState(), "serviceInProgressId"))
                .taskId(getIntegerFromMap(transaction.getCurrentState(), "taskId"))
                .materialId(getIntegerFromMap(transaction.getCurrentState(), "materialId"))
                .materialReviewId(getIntegerFromMap(transaction.getCurrentState(), "materialReviewId"))
                .timestamp(Instant.now())
                .build();

        AuditLog savedRollbackAuditLog = auditLogRepository.save(rollbackAuditLog);
        auditLogPublisher.publish(savedRollbackAuditLog);

        return true;
    }

    /**
     * Restore an entity to a specific point in time.
     *
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @param timestamp Target timestamp (ISO format)
     * @return ID of the rollback transaction
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
     * Get the list of transactions that can be rolled back for a specific entity.
     *
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @return List of transactions that can be rolled back
     */
    public List<TransactionLog> getRollbackCandidates(AuditEntity entityType, Integer entityId) {
        return transactionLogRepository.findByEntityTypeAndEntityIdAndRolledBackFalseOrderByTimestampDesc(
                entityType, entityId);
    }

    /**
     * Get the transaction history for a specific entity.
     *
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @return List of all transactions for the entity
     */
    public List<TransactionLog> getTransactionHistory(AuditEntity entityType, Integer entityId) {
        return transactionLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    /**
     * Get the transaction history for all entities related to a project.
     *
     * @param projectId ID of the project
     * @return List of all transactions related to the project
     */
    public List<TransactionLog> getTransactionHistoryByProject(Integer projectId) {
        return transactionLogRepository.findByProjectIdOrderByTimestampDesc(projectId);
    }

    /**
     * Get transactions within a time range for an entity.
     *
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @param startTime Start of time range
     * @param endTime End of time range
     * @return List of transactions within the time range
     */
    public List<TransactionLog> getTransactionsInTimeRange(
            AuditEntity entityType, Integer entityId, Instant startTime, Instant endTime) {
        return transactionLogRepository.findByEntityTypeAndEntityIdAndTimestampBetweenOrderByTimestampDesc(
                entityType, entityId, startTime, endTime);
    }

    /**
     * Get transactions from the last N hours for an entity.
     *
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @param hours Number of hours to look back
     * @return List of transactions from the last N hours
     */
    public List<TransactionLog> getRecentTransactions(AuditEntity entityType, Integer entityId, int hours) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(hours, ChronoUnit.HOURS);

        return getTransactionsInTimeRange(entityType, entityId, startTime, endTime);
    }

    /**
     * Get the inverse action for rollback purposes.
     */
    private AuditAction getInverseAction(AuditAction action) {
        return switch (action) {
            case CREATE -> AuditAction.DELETE;
            case DELETE -> AuditAction.CREATE;
            case UPDATE -> AuditAction.UPDATE; // For update, the inverse is another update
            case ROLLBACK -> null;
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

    /**
     * Get integer from a map safely.
     */
    private Integer getIntegerFromMap(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return null;
        }

        Object value = map.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return null;
    }
}