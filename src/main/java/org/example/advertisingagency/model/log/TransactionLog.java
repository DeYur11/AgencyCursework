package org.example.advertisingagency.model.log;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Enhanced transaction log model that combines functionality of AuditLog and TransactionLog.
 * Supports both real-time events and rollback operations.
 */
@Setter
@Builder
@Document(collection = "transaction_log")
@Getter
public class TransactionLog {
    @Id
    private String id;

    // Entity information
    private AuditEntity entityType;
    private Integer entityId;

    // Action that was performed
    private AuditAction action;

    // Who performed the action
    private Integer workerId;
    private String username;
    private String role;

    // The state before the change (for rollback)
    private Map<String, Object> previousState;

    // The state after the change (for audit)
    private Map<String, Object> currentState;

    // Transaction timestamp
    private Instant timestamp;

    // Whether this transaction has been rolled back
    private boolean rolledBack;

    // If rolled back, reference to the rollback transaction
    private String rollbackTransactionId;

    // Description of the transaction
    private String description;

    // Related entity IDs for filtering and subscriptions
    private Integer projectId;
    private Integer serviceInProgressId;
    private Integer taskId;
    private Integer materialId;
    private Integer materialReviewId;

    public Boolean getRolledBack() {
        return rolledBack;
    }
}