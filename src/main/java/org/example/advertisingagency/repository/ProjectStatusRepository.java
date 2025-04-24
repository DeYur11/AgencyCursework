package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Integer> {
}