package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTypeRepository extends JpaRepository<ProjectType, Integer> {
}