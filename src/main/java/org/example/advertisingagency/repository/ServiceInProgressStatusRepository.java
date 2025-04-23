package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ServiceInProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceInProgressStatusRepository extends JpaRepository<ServiceInProgressStatus, Integer> {
}