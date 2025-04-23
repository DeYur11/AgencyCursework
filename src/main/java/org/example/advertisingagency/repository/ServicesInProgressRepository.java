package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.ServicesInProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicesInProgressRepository extends JpaRepository<ServicesInProgress, Integer> {
}