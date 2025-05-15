package org.example.advertisingagency.controller;

import org.example.advertisingagency.dto.rollback.RollbackInput;
import org.example.advertisingagency.dto.rollback.RollbackResponse;
import org.example.advertisingagency.dto.rollback.TransactionLogDTO;
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
 * Controller for rollback operations.
 */
@Controller
public class RollbackController {

    private final TransactionLogService transactionLogService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public RollbackController(TransactionLogService transactionLogService) {
        this.transactionLogService = transactionLogService;
    }

    /**
     * Get transactions that can be rolled back for a specific entity.
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
        } catch (Exception e) {
            return new RollbackResponse(
                    false,
                    "Rollback failed: " + e.getMessage(),
                    input.transactionId()
            );
        }
    }

    /**
     * Convert a TransactionLog entity to a DTO.
     */
    private TransactionLogDTO convertToDTO(TransactionLog log) {
        return new TransactionLogDTO(
                log.getId(),
                log.getEntityType().name(),
                log.getEntityId(),
                log.getAction().name(),
                log.getWorkerId(),
                log.getUsername(),
                log.getTimestamp().toString(),
                log.isRolledBack()
        );
    }
}