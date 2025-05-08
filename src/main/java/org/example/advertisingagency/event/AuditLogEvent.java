package org.example.advertisingagency.event;

import lombok.Getter;
import org.example.advertisingagency.model.auth.AuditAction;
import org.example.advertisingagency.model.auth.AuditEntity;
import org.example.advertisingagency.model.auth.AuditLog;
import org.springframework.context.ApplicationEvent;

@Getter
public class AuditLogEvent extends ApplicationEvent {

    private final Integer workerId;
    private final String username;
    private final String role;
    private final AuditAction action;
    private final AuditEntity entity;
    private final String description;
    private final Integer projectId;
    private final Integer taskId;
    private final Integer materialId;

    public AuditLogEvent(Object source,
                         Integer workerId, String username, String role,
                         AuditAction action, AuditEntity entity,
                         String description,
                         Integer projectId, Integer taskId, Integer materialId) {
        super(source);
        this.workerId = workerId;
        this.username = username;
        this.role = role;
        this.action = action;
        this.entity = entity;
        this.description = description;
        this.projectId = projectId;
        this.taskId = taskId;
        this.materialId = materialId;
    }

    public AuditLogEvent(Object source, AuditLog log) {
        super(source);
        this.workerId = log.getWorkerId();
        this.username = log.getUsername();
        this.role = log.getRole();
        this.action = log.getAction();
        this.entity = log.getEntity();
        this.description = log.getDescription();
        this.projectId = log.getProjectId();
        this.taskId = log.getTaskId();
        this.materialId = log.getMaterialId();
    }
}
