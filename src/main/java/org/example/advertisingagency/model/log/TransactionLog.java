package org.example.advertisingagency.model.log;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a transaction log that stores the state of entities before and after changes,
 * enabling rollback capabilities.
 */
@Getter
@Setter
@Builder
@Document(collection = "transaction_log")
public class TransactionLog {
    @Id
    private String id;

    // Connection to the audit log for reference
    private String auditLogId;

    // Entity information
    private AuditEntity entityType;
    private Integer entityId;

    // Action that was performed
    private AuditAction action;

    // Who performed the action
    private Integer workerId;
    private String username;

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

    private String description;
}