package org.example.advertisingagency.service.logs;

import org.example.advertisingagency.model.auth.AuditAction;
import org.example.advertisingagency.model.auth.AuditEntity;
import org.example.advertisingagency.model.auth.AuditLog;
import org.example.advertisingagency.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogPublisher auditLogPublisher;

    public AuditLogService(AuditLogRepository repo, AuditLogRepository auditLogRepository, AuditLogPublisher auditLogPublisher) {
        this.repo = repo;
        this.auditLogRepository = auditLogRepository;
        this.auditLogPublisher = auditLogPublisher;
    }

    public void logAction(Integer workerId, String username, String role,
                          AuditAction action, AuditEntity entity,
                          String description,
                          Integer projectId, Integer taskId, Integer materialId) {

        AuditLog createdLog = AuditLog.builder()
                .workerId(workerId)
                .username(username)
                .role(role)
                .action(action)
                .entity(entity)
                .description(description)
                .projectId(projectId)
                .taskId(taskId)
                .materialId(materialId)
                .timestamp(Instant.now())
                .build();
        this.log(createdLog);
    }

    public List<AuditLog> getByProjectIds(List<Integer> ids) {
        return auditLogRepository.findTop100ByProjectIdInOrderByTimestampDesc(ids);
    }

    public List<AuditLog> getByMaterialIds(List<Integer> ids) {
        return auditLogRepository.findTop100ByMaterialIdInOrderByTimestampDesc(ids);
    }

    public List<AuditLog> getTaskRelatedLogs() {
        return auditLogRepository.findTop100ByEntityAndTaskIdIsNotNullOrderByTimestampDesc(AuditEntity.TASK);
    }

    public void log(AuditLog log) {
        auditLogRepository.save(log);
        System.out.println("💾 Saved and now publishing...");
        auditLogPublisher.publish(log);
    }
}
