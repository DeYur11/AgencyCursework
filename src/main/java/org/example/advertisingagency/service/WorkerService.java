package org.example.advertisingagency.service;

import org.example.advertisingagency.model.*;
import org.example.advertisingagency.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MaterialReviewRepository materialReviewRepository;

    public WorkerService(WorkerRepository workerRepository,
                         ProjectRepository projectRepository,
                         TaskRepository taskRepository,
                         MaterialReviewRepository materialReviewRepository) {
        this.workerRepository = workerRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.materialReviewRepository = materialReviewRepository;
    }

    public List<Worker> findAll() {
        return workerRepository.findAll();
    }

    public Optional<Worker> findOne(Integer id) {
        return workerRepository.findById(id);
    }

    public Worker create(Worker worker) {
        return workerRepository.save(worker);
    }

    public Optional<Worker> update(Integer id, Worker updated) {
        return workerRepository.findById(id).map(w -> {
            w.setName(updated.getName());
            w.setSurname(updated.getSurname());
            w.setEmail(updated.getEmail());
            w.setPhoneNumber(updated.getPhoneNumber());
            w.setIsReviewer(updated.getIsReviewer());
            w.setPosition(updated.getPosition());
            w.setOffice(updated.getOffice());
            w.setUpdateDatetime(updated.getUpdateDatetime());
            return workerRepository.save(w);
        });
    }

    public boolean delete(Integer id) {
        return workerRepository.findById(id).map(w -> {
            workerRepository.delete(w);
            return true;
        }).orElse(false);
    }

    public Position getPosition(Integer workerId) {
        return workerRepository.findById(workerId)
                .map(Worker::getPosition)
                .orElse(null);
    }

    public Office getOffice(Integer workerId) {
        return workerRepository.findById(workerId)
                .map(Worker::getOffice)
                .orElse(null);
    }

    public List<Project> getManagedProjects(Integer workerId) {
        return projectRepository.findAllByManager_Id(workerId);
    }

    public List<Task> getAssignedTasks(Integer workerId) {
        return taskRepository.findAllByAssignedWorker_Id(workerId);
    }

    public List<MaterialReview> getMaterialReviews(Integer workerId) {
        return materialReviewRepository.findAllByReviewer_Id(workerId);
    }
}
