package org.example.advertisingagency.service.logs;

import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.example.advertisingagency.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogPublisher auditLogPublisher;

    public AuditLogService(AuditLogRepository auditLogRepository, AuditLogPublisher auditLogPublisher) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogPublisher = auditLogPublisher;
    }

    public List<AuditLog> getFilteredAuditsByProjectIds(List<Integer> ids) {
        return auditLogRepository.findTop100ByProjectIdInOrderByTimestampDesc(ids);
    }

    public List<AuditLog> getByMaterialIds(List<Integer> ids) {
        return auditLogRepository.findTop100ByEntityAndMaterialIdInOrderByTimestampDesc(AuditEntity.MATERIAL_REVIEW,ids);
    }

    public List<AuditLog> getTaskRelatedLogs(List<Integer> tasks) {
        return auditLogRepository.findTop100ByEntityInAndTaskIdInOrderByTimestampAsc(List.of(AuditEntity.MATERIAL_REVIEW, AuditEntity.MATERIAL), tasks);
    }

    public void log(AuditLog log) {
        auditLogRepository.save(log);
        System.out.println("💾 Saved and now publishing...");
        auditLogPublisher.publish(log);
    }
}
