package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.rollback.RollbackInput;
import org.example.advertisingagency.dto.rollback.RollbackResponse;
import org.example.advertisingagency.dto.rollback.TransactionHistoryInput;
import org.example.advertisingagency.dto.rollback.TransactionLogDTO;
import org.example.advertisingagency.exception.RollbackException;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.TransactionLog;
import org.example.advertisingagency.service.logs.TransactionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for transaction history and rollback operations.
 */
@Controller
public class TransactionHistoryController {

    private final TransactionLogService transactionLogService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public TransactionHistoryController(TransactionLogService transactionLogService) {
        this.transactionLogService = transactionLogService;
    }

    /**
     * Get rollback candidates for a specific entity.
     */
    @QueryMapping
    public List<TransactionLogDTO> getRollbackCandidates(
            @Argument String entityType,
            @Argument Integer entityId) {

        AuditEntity auditEntity = AuditEntity.valueOf(entityType);
        List<TransactionLog> transactions = transactionLogService.getRollbackCandidates(auditEntity, entityId);

        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get transaction history for a specific entity.
     */
    @QueryMapping
    public List<TransactionLogDTO> getTransactionHistory(@Argument TransactionHistoryInput input) {
        AuditEntity auditEntity = AuditEntity.valueOf(input.getEntityType());
        List<TransactionLog> transactions;

        if (input.getEntityId() != null) {
            // Get history for specific entity
            transactions = transactionLogService.getTransactionHistory(auditEntity, input.getEntityId());
        } else if (input.getProjectId() != null) {
            // Get history for all entities related to a project
            transactions = transactionLogService.getTransactionHistoryByProject(input.getProjectId());
        } else {
            throw new IllegalArgumentException("Either entityId or projectId must be provided");
        }

        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Rollback a specific transaction.
     */
    @MutationMapping
    public RollbackResponse rollbackTransaction(@Argument RollbackInput input) {
        try {
            boolean success = transactionLogService.rollbackTransaction(input.transactionId());
            return new RollbackResponse(
                    success,
                    success ? "Transaction successfully rolled back" : "Rollback failed",
                    input.transactionId()
            );
        } catch (RollbackException e) {
            return new RollbackResponse(
                    false,
                    "Rollback failed: " + e.getMessage(),
                    input.transactionId()
            );
        } catch (Exception e) {
            return new RollbackResponse(
                    false,
                    "Unexpected error during rollback: " + e.getMessage(),
                    input.transactionId()
            );
        }
    }

    /**
     * Restore an entity to a specific point in time.
     */
    @MutationMapping
    public RollbackResponse restoreEntityToPoint(
            @Argument String entityType,
            @Argument Integer entityId,
            @Argument String timestamp) {
        try {
            String transactionId = transactionLogService.restoreEntityToPoint(
                    AuditEntity.valueOf(entityType),
                    entityId,
                    timestamp
            );

            return new RollbackResponse(
                    true,
                    "Entity successfully restored to the specified point in time",
                    transactionId
            );
        } catch (Exception e) {
            return new RollbackResponse(
                    false,
                    "Restore failed: " + e.getMessage(),
                    null
            );
        }
    }

    /**
     * Convert a TransactionLog entity to a DTO.
     */
    private TransactionLogDTO convertToDTO(TransactionLog log) {
        return TransactionLogDTO.builder()
                .id(log.getId())
                .entityType(log.getEntityType().name())
                .entityId(log.getEntityId())
                .action(log.getAction().name())
                .workerId(log.getWorkerId())
                .username(log.getUsername())
                .timestamp(log.getTimestamp().toString())
                .description(getDescription(log))
                .rolledBack(log.isRolledBack())
                .previousState(log.getPreviousState() != null)
                .currentState(log.getCurrentState() != null)
                .build();
    }

    /**
     * Get a meaningful description from the transaction log.
     */
    private String getDescription(TransactionLog log) {
        StringBuilder description = new StringBuilder();

        // Add basic info
        description.append(log.getAction()).append(" ").append(log.getEntityType());

        // Add entity name if available
        if (log.getCurrentState() != null && log.getCurrentState().containsKey("name")) {
            description.append(": ").append(log.getCurrentState().get("name"));
        } else if (log.getPreviousState() != null && log.getPreviousState().containsKey("name")) {
            description.append(": ").append(log.getPreviousState().get("name"));
        } else {
            description.append(" ID: ").append(log.getEntityId());
        }

        return description.toString();
    }
}