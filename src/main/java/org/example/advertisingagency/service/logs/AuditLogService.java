package org.example.advertisingagency.service.logs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.advertisingagency.model.log.AuditAction;
import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.example.advertisingagency.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogPublisher auditLogPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON (де)сереалізація

    public List<AuditLog> getFilteredAuditsByProjectIds(List<Integer> ids) {
        return auditLogRepository.findTop100ByProjectIdInOrderByTimestampDesc(ids);
    }

    public List<AuditLog> getByMaterialIds(List<Integer> ids) {
        return auditLogRepository.findTop100ByEntityAndMaterialIdInOrderByTimestampDesc(AuditEntity.MATERIAL_REVIEW, ids);
    }

    public List<AuditLog> getTaskRelatedLogs(List<Integer> tasks) {
        return auditLogRepository.findTop100ByEntityInAndTaskIdInOrderByTimestampAsc(
                List.of(AuditEntity.MATERIAL_REVIEW, AuditEntity.MATERIAL), tasks);
    }

    public void log(AuditLog log) {
        auditLogRepository.save(log);
        System.out.println("💾 Saved and now publishing...");
        auditLogPublisher.publish(log);
    }

    /**
     * Відкотити дію за логом
     */
    public void rollback(String auditLogId, String username) {
        AuditLog original = auditLogRepository.findById(auditLogId)
                .orElseThrow(() -> new RuntimeException("❌ Audit log not found"));

        if (Boolean.TRUE.equals(original.getReverted())) {
            throw new IllegalStateException("⚠️ This action was already reverted");
        }

        AuditLog rollback = AuditLog.builder()
                .action(AuditAction.ROLLBACK)
                .entity(original.getEntity())
                .username(username)
                .timestamp(Instant.now())
                .beforeState(original.getAfterState())
                .afterState(original.getBeforeState())
                .description("Rollback of logId=" + auditLogId)
                .reverted(false)
                .revertedByLogId(original.getId())

                // прокидаємо ідентифікатори
                .materialId(original.getMaterialId())
                .materialReviewId(original.getMaterialReviewId())
                .taskId(original.getTaskId())
                .projectId(original.getProjectId())
                .serviceInProgressId(original.getServiceInProgressId())
                .materialReviewId(original.getMaterialReviewId())

                .build();

        auditLogRepository.save(rollback);
        auditLogPublisher.publish(rollback);

        original.setReverted(true);
        auditLogRepository.save(original);

        System.out.println("✅ Rollback complete for log ID: " + auditLogId);
    }
}
