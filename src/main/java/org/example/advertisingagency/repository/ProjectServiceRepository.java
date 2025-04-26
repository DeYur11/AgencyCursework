package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ProjectService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectServiceRepository extends JpaRepository<ProjectService, Integer> {
    List<ProjectService> findAllByServiceID_Id(Integer serviceId);
    List<ProjectService> findAllByProjectID_Id(Integer projectId);
}