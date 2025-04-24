package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    List<Worker> findAllByPositionId(Integer position);
}