package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Worker;
import org.hibernate.jdbc.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    List<Worker> findAllByPosition_Id(Integer positionId);
    List<Worker> findAllByOffice_Id(Integer officeId);
    List<Worker> findByPositionNameIgnoreCase(String positionName);
}