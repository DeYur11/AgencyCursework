package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.MaterialStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialStatusRepository extends JpaRepository<MaterialStatus, Integer> {
}