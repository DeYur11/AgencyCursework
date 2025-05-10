package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Integer> {
    Optional<ProjectStatus> findByName(String name);
}