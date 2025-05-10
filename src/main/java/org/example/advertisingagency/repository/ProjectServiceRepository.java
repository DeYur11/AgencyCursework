package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ProjectService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectServiceRepository extends JpaRepository<ProjectService, Integer>, JpaSpecificationExecutor<ProjectService> {
    List<ProjectService> findAllByService_Id(Integer serviceId);
    List<ProjectService> findAllByProject_Id(Integer projectId);

}