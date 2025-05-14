package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Material;
import org.example.advertisingagency.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
    List<Material> findAllByLanguage_Id(Integer languageId);
    List<Material> findAllByTargetAudience_Id(Integer targetAudienceId);
    List<Material> findAllByLicenceType_Id(Integer licenceTypeId);
    List<Material> findAllByStatus_Id(Integer statusId);
    List<Material> findAllByMaterialType_Id(Integer typeId);
    List<Material> findAllByUsageRestriction_Id(Integer usageRestrictionId);
    List<Material> findAllByTask_Id(Integer taskId);
    Page<Material> findAll(Specification<Material> spec, Pageable pageable);
    @EntityGraph(attributePaths = {"type", "language", "licenceType"})
    List<Material> findAll(Specification<Material> spec, Sort sort);

    @Query("""
    SELECT m
    FROM Material m
    WHERE m.task.assignedWorker.id = :workerId
""")
    List<Material> findAllByAssignedWorkerId(@Param("workerId") Integer workerId);

}