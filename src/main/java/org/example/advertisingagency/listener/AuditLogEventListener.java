package org.example.advertisingagency.listener;

import org.example.advertisingagency.event.AuditLogEvent;
import org.example.advertisingagency.model.auth.AuditLog;
import org.example.advertisingagency.repository.AuditLogRepository;
import org.example.advertisingagency.service.logs.AuditLogService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuditLogEventListener {

    private final AuditLogService auditLogService;

    public AuditLogEventListener(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @EventListener
    public void handleAuditLogEvent(AuditLogEvent event) {
        auditLogService.logAction(
                event.getWorkerId(),
                event.getUsername(),
                event.getRole(),
                event.getAction(),
                event.getEntity(),
                event.getDescription(),
                event.getProjectId(),
                event.getTaskId(),
                event.getMaterialId()
        );
    }
}
