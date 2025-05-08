package org.example.advertisingagency.model.auth;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@Document(collection = "audit_log")
public class AuditLog {
    @Id
    private String id;

    private Integer workerId;
    private String username;
    private String role;

    private AuditAction action;
    private AuditEntity entity;

    private String description;
    private Integer projectId;
    private Integer taskId;
    private Integer materialId;
    private Instant timestamp;
}
