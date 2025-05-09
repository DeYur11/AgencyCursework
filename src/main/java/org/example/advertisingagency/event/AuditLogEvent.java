package org.example.advertisingagency.event;

import lombok.Getter;
import lombok.Setter;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class AuditLogEvent extends ApplicationEvent {

    private AuditLog auditLog;

    public AuditLogEvent(Object source, AuditLog log) {
        super(source);
        this.auditLog = log;
    }
}
