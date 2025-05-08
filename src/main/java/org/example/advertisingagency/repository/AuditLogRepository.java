package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.auth.AuditEntity;
import org.example.advertisingagency.model.auth.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByWorkerId(Integer workerId);
    List<AuditLog> findByProjectId(Integer projectId);
    List<AuditLog> findByTaskId(Integer taskId);
    List<AuditLog> findTop100ByProjectIdInOrderByTimestampDesc(List<Integer> projectIds);
    List<AuditLog> findTop100ByMaterialIdInOrderByTimestampDesc(List<Integer> materialIds);
    List<AuditLog> findTop100ByEntityAndTaskIdIsNotNullOrderByTimestampDesc(AuditEntity entity);
}

