package org.example.advertisingagency.listener;

import org.example.advertisingagency.event.AuditLogEvent;
import org.example.advertisingagency.service.logs.AuditLogService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuditLogEventListener {

    private final AuditLogService auditLogService;

    public AuditLogEventListener(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @EventListener
    public void handleAuditLogEvent(AuditLogEvent event) {
        auditLogService.log(event.getAuditLog());
    }
}
