package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.MaterialStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialStatusRepository extends JpaRepository<MaterialStatus, Integer> {
    Optional<MaterialStatus> findByName(String name);
}