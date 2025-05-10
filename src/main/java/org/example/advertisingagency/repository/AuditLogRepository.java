package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.log.AuditEntity;
import org.example.advertisingagency.model.log.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByWorkerId(Integer workerId);
    List<AuditLog> findByProjectId(Integer projectId);
    List<AuditLog> findByTaskId(Integer taskId);


    @Query("""
{
  "taskId": { "$in": ?1 },
  "entity": { "$in": ?0 }
}
""")
    List<AuditLog> findTop100ByEntityInAndTaskIdInOrderByTimestampAsc(List<AuditEntity> allowedEntities, List<Integer> taskIds);
    @Query("""
{
  "serviceInProgressId": { "$in": ?1 },
  "entity": { "$in": ?0 }
}
""")
    List<AuditLog> findTop100ByEntityInAndServiceInProgressIdInOrderByTimestampDesc(List<AuditEntity> allowedEntities, List<Integer> serviceIds);
    @Query("""
{
  "materialId": { "$in": ?1 },
  "entity": { "$in": ?0 }
}
""")
    List<AuditLog> findTop100ByEntityInAndMaterialIdInOrderByTimestampDesc(List<AuditEntity> allowedEntities, List<Integer> materialIds);
    @Query("""
{
  "projectId": { "$in": ?1 },
  "entity": { "$in": ?0 }
}
""")
    List<AuditLog> findTop100ByEntityInAndProjectIdInOrderByTimestampDesc(List<AuditEntity> allowedEntities, List<Integer> projectIds);
    @Query("""
{
  "materialId": { "$in": ?1 },
  "entity": { "$eq": ?0 }
}
""")
    List<AuditLog> findTop100ByEntityAndMaterialIdInOrderByTimestampDesc(AuditEntity entity, List<Integer> materialIds);

    List<AuditLog> findTop100ByProjectIdInOrderByTimestampDesc(List<Integer> projectIds);
    List<AuditLog> findTop100ByMaterialIdInOrderByTimestampDesc(List<Integer> materialIds);
    List<AuditLog> findTop100ByEntityAndTaskIdIsNotNullOrderByTimestampDesc(AuditEntity entity);
}

