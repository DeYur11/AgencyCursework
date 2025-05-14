package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
  List<Project> findAllByClient_Id(Integer clientID);
  List<Project> findAllByManager_Id(Integer workerId);
  List<Project> findAllByProjectType_Id(Integer projectType);
  List<Project> findAllByStatus_Id(Integer statusId);

  @Modifying
  @Query(value = "EXEC usp_UpdateProjectStatusWithCascade :projectId, :newStatusId", nativeQuery = true)
  void executeStatusUpdateProcedure(@Param("projectId") Integer projectId, @Param("newStatusId") Integer newStatusId);

    Page<Project> findAll(Specification<Project> spec, Pageable pageable);
}