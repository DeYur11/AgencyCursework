package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ServiceInProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceInProgressStatusRepository extends JpaRepository<ServiceInProgressStatus, Integer> {
    Optional<ServiceInProgressStatus> findByName(final String name);
}