package org.example.advertisingagency.event;

import lombok.Getter;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Event published when a transaction rollback is requested.
 */
@Getter
public class RollbackEvent extends ApplicationEvent {
    private final AuditEntity entityType;
    private final Integer entityId;
    private final AuditAction rollbackAction;
    private final Map<String, Object> previousState;
    private final String originatingTransactionId;

    public RollbackEvent(
            Object source,
            AuditEntity entityType,
            Integer entityId,
            AuditAction rollbackAction,
            Map<String, Object> previousState,
            String originatingTransactionId) {
        super(source);
        this.entityType = entityType;
        this.entityId = entityId;
        this.rollbackAction = rollbackAction;
        this.previousState = previousState;
        this.originatingTransactionId = originatingTransactionId;
    }
}