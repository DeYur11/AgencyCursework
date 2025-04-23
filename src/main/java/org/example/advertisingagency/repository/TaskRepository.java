package org.example.advertisingagency.repository;

import org.example.advertisingagency.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByAssignedWorker_Id(int id);
}