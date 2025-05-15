package org.example.advertisingagency.dto.rollback;

import lombok.Builder;

/**
 * DTO for transaction logs.
 */
@Builder
public record TransactionLogDTO(
        String id,
        String entityType,
        Integer entityId,
        String action,
        Integer workerId,
        String username,
        String timestamp,
        String description,
        boolean rolledBack,
        boolean previousState,
        boolean currentState
) {
    public String getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public String getAction() {
        return action;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public String getUsername() {
        return username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRolledBack() {
        return rolledBack;
    }

    public boolean hasPreviousState() {
        return previousState;
    }

    public boolean hasCurrentState() {
        return currentState;
    }
}