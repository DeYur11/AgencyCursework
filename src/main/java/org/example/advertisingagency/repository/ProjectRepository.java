package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
  List<Project> findAllByClient_Id(Integer clientID);
  List<Project> findAllByManager_Id(Integer workerId);
  List<Project> findAllByProjectType_Id(Integer projectType);
  List<Project> findAllByStatus_Id(Integer statusId);
}